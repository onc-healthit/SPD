from itertools import groupby

import toolz
from oslash import Just

from nppes_data_generators.names.networks import synthetic_network_name_generator
from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline
from nppes_data_loaders.migration.identifier import migrate_identifier, identity_transformer

'''
Within the same transaction:
1. Migrate vhdir_network: synthesize name and alias and keep the rest the same
2. Migrate identifier
3. Migrate telecom
4. Migrate address
5. Migrate resource_reference ***NOT HERE***
6. Migrate contact ***NOT HERE***
'''
def migrate_vhdir_network():
    extract_stmt = """SELECT n.network_id, n.meta_data_id, n.active, n.period_start, n.period_end, 
                        n.part_of_resource_reference_id, n.practitioner_role_id, n.organization_affiliation_id, 
                        n.coverage_id, n.plan_id, n.alias, n.name, a.state 
                      FROM vhdir_network n 
                      JOIN (SELECT network_id, COALESCE(GROUP_CONCAT(state)) AS state 
                            FROM address GROUP BY network_id) a 
                      ON n.network_id=a.network_id 
                      ORDER BY a.state;"""

    load_stmt = """INSERT INTO vhdir_network (network_id, meta_data_id, active, period_start, period_end, 
                    part_of_resource_reference_id, practitioner_role_id, organization_affiliation_id, coverage_id, 
                    plan_id, name, alias) VALUES ({})""".format(('%s,'*12)[:-1])

    @toolz.curry
    def transform(_, networks):
        name_synthesizer = synthetic_network_name_generator

        by_state = (_[1] + (_[0],) for group in groupby(networks, lambda n: n[-1]) for _ in enumerate(group[1]))
        with_name = (_[:-4] + (name_synthesizer(_[-3], _[-2], _[-1]), name_synthesizer(_[-4], _[-2], _[-1])) for _ in
                     by_state)

        return Just(with_name)

    return SQLJob(extract_stmt, transform, load_stmt)


def migrate_networks(connections):
    return SQLPipeline(migrate_vhdir_network(),
                       migrate_identifier('network_id', 'https://www.cms.gov/CCIIO/', identity_transformer),
                       setup='SET FOREIGN_KEY_CHECKS=0;',
                       teardown='SET FOREIGN_KEY_CHECKS=1;')\
        .run(connections)
