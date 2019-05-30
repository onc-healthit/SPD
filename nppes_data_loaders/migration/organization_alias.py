import toolz
from oslash import Just

from nppes_data_generators.names.organizations import synthetic_org_name_generator
from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def migrate_organization_alias(connections):
    extract_stmt = """
        SELECT COALESCE(GROUP_CONCAT(DISTINCT t.value), ''), a.* FROM spd_small.organization_alias a
        JOIN organization_taxonomy t ON a.organization_id = t.organization_id
        GROUP BY a.organization_id
        ORDER BY a.value;
    """

    load_stmt = 'INSERT INTO organization_alias VALUES ({});'.format(('%s,'*6)[:-1])

    @toolz.curry
    def transform(_, aliases):
        name_synthesizer = synthetic_org_name_generator()

        def scrub(record):
            taxonomies, organization_alias_id, period_start, period_end, value, *rest = record
            return (organization_alias_id, period_start, period_end,
                    name_synthesizer(value, *taxonomies.split(',') if taxonomies else []), *rest)

        return Just(map(scrub, aliases))

    return SQLJob(extract_stmt, transform, load_stmt).run(connections)
