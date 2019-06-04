import toolz
from oslash import Just

from nppes_data_generators.phone_numbers.phone_number import synthetic_number
from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def migrate_telecom(connections):
    '''
    Extract: Pull all telecom records from original DB
    Transform: Synthesize the value field using :func:nppes_data_generators.phone_numbers.phone_number.synthetic_number
    Load: Push synthesized records to scrubbed DB

    :param connections: Two-connection tuple to know where to pull the data from and here to push it back to
    :return: Just(connections) if everything went well else Nothing()
    '''
    extract_stmt = 'SELECT * FROM telecom;'
    load_stmt = 'INSERT INTO telecom VALUES ({});'.format(('%s,'*16)[:-1])

    def transform(_, telecoms):

        def scrub_number(telecom):
            telecom_id, system, value, *rest = telecom
            return (telecom_id, system, synthetic_number(value), *rest)

        res = Just(list(map(scrub_number, telecoms)))

        return res

    return SQLJob(extract_stmt, transform, load_stmt).run(connections)
