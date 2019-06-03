import toolz
from oslash import Just

from nppes_data_generators.names.organizations import synthetic_org_name_generator
from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline
from nppes_data_loaders.migration.identifier import migrate_identifier, npi_transformer, \
    hios_issuer_id_transformer
from tests.utils import timing

'''
Within the same transaction:
1. Migrate vhdir_organization: organization_id, name, active, partOf_organization_id
2. Migrate identifier: use, system, organization_id, value
3. Migrate telecom: organization_id, system, value
4. Migrate address: organization_id, use, type, line1, city, district, state, postalCode, country, latitude, longitude
5. Migrate organization_contact: organization_id, purpose_cc_id, name_id, address_id
'''
def migrate_vhdir_organization():
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

    @toolz.curry
    @timing
    def transform(_, orgs):
        print('Entering transform')
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

        '''
         list(orgs) here to avoid keeping connection writing to net too long
        '''
        without_partOf_name = {_[0]: _ for _ in map(scrub_org, orgs)}
        with_partOf_name = map(attach_partOf_name(without_partOf_name), without_partOf_name.values())
        return Just(with_partOf_name)

    return SQLJob(extract_stmt, transform, load_stmt)


def migrate_organizations(connections):
    return SQLPipeline(migrate_vhdir_organization(),
                       migrate_identifier('organization_id', 'http://hl7.org/fhir/sid/us-npi', npi_transformer),
                       migrate_identifier('organization_id', 'https://www.cms.gov/CCIIO/', hios_issuer_id_transformer),
                       setup='SET FOREIGN_KEY_CHECKS=0;',
                       teardown='SET FOREIGN_KEY_CHECKS=1;')\
        .run(connections)
