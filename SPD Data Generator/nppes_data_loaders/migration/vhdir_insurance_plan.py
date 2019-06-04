from oslash import Just

from nppes_data_generators.names.insurance_plans import synthetic_insurance_plan_name_generator
from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline


def migrate_vhdir_insurance_plan():
    '''
    Extract: Pull all addresses from original DB
    Transform: Synthesize address line1 for each address
    Load: Push synthesized addresses to scrubbed DB

    :param connections: Two-connection tuple to know where to pull the data from and here to push it back to
    :return: Just(connections) if everything went well else Nothing()
    '''
    extract_stmt = 'SELECT * FROM vhdir_insurance_plan;'

    load_stmt = 'INSERT INTO vhdir_insurance_plan VALUES ({});'.format(('%s,'*10)[:-1])

    def transform(_, plans):
        name_synthesizer = synthetic_insurance_plan_name_generator()

        def scrub_plan(idx_and_record):
            idx, record = idx_and_record
            ip_id, md_id, status, name, *rest = record
            return (ip_id, md_id, status, name_synthesizer(idx, name), *rest)

        return Just(map(scrub_plan, enumerate(plans)))

    return SQLJob(extract_stmt, transform, load_stmt)


def migrate_insurance_plans(connections):
    '''
    Need to turn off FK checks (setup/teardown) due to table inter-dependencies.

    :param connections:  Two-connection tuple to know where to pull the data from and here to push it back to
    :return:
    '''
    return SQLPipeline(migrate_vhdir_insurance_plan(),
                       setup='SET FOREIGN_KEY_CHECKS=0;',
                       teardown='SET FOREIGN_KEY_CHECKS=1;')\
        .run(connections)
