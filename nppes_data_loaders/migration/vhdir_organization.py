import toolz
from oslash import Just

from nppes_data_generators.names.organizations import synthetic_org_name_generator
from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline
from nppes_data_loaders.migration.identifier import migrate_identifier, npi_transformer, \
    hios_issuer_id_transformer


def migrate_vhdir_organization():
    '''
    Extract: Pull all organizations from original DB, along with their taxonomies
    Transform: Synthesize the name field using the
        :func:nppes_data_generators.names.organizations.synthetic_org_name_generator, then attach the name of the
        parent organization if any
    Load: Push synthesized organizations to scrubbed DB

    :param connections: Two-connection tuple to know where to pull the data from and here to push it back to
    :return: Just(connections) if everything went well else Nothing()
    '''
    extract_stmt = """
            SELECT o.organization_id, o.name, COALESCE(GROUP_CONCAT(DISTINCT t.value), ''), 
                active, partOf_organization_id
            FROM vhdir_organization o
            LEFT JOIN organization_taxonomy t ON o.organization_id = t.organization_id
            GROUP BY o.organization_id
            ORDER BY name;
            """
    load_stmt = """INSERT INTO vhdir_organization (organization_id, name, active, partOf_organization_id, 
                partOf_organization_name) VALUES (%s, %s, %s, %s, %s);"""

    def transform(_, orgs):
        name_synthesizer = synthetic_org_name_generator()

        def scrub_org(org):
            organization_id, name, taxonomies, active, partOf_organization_id = org
            if organization_id % 10000 == 0:
                print(organization_id)
            return organization_id, name_synthesizer(name, *taxonomies.split(',') if taxonomies else []), active, \
                   partOf_organization_id

        @toolz.curry
        def attach_partOf_name(d, org):
            partOf_name = (d.get(org[-1])[1],) if d.get(org[-1]) else (None,)
            return org + partOf_name

        # Keep dict of generated names to attach parent org. names afterward
        without_partOf_name = {_[0]: _ for _ in map(scrub_org, orgs)}
        with_partOf_name = map(attach_partOf_name(without_partOf_name), without_partOf_name.values())
        return Just(with_partOf_name)

    return SQLJob(extract_stmt, transform, load_stmt)


def migrate_organizations(connections):
    '''
    Migrate organizations first, then identifiers belonging to NPPES orgs, then those belonging to CCIIO orgs.

    Need to turn off FK checks (setup/teardown) due to intra-dependencies. For instance, an org. might refer to its
    parent that has not been loaded yet.

    :param connections:
    :return:
    '''
    return SQLPipeline(migrate_vhdir_organization(),
                       migrate_identifier('organization_id', 'http://hl7.org/fhir/sid/us-npi', npi_transformer),
                       migrate_identifier('organization_id', 'https://www.cms.gov/CCIIO/', hios_issuer_id_transformer),
                       setup='SET FOREIGN_KEY_CHECKS=0;',
                       teardown='SET FOREIGN_KEY_CHECKS=1;')\
        .run(connections)
