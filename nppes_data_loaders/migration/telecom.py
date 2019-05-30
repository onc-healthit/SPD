import toolz
from oslash import Just

from nppes_data_generators.phone_numbers.phone_number import synthetic_number
from nppes_data_loaders.etl.etl import SQLJob
from tests.utils import timing


@toolz.curry
def migrate_telecom(connections):
    extract_stmt = 'SELECT * FROM telecom;'
    load_stmt = 'INSERT INTO telecom VALUES ({});'.format(('%s,'*16)[:-1])

    @toolz.curry
    @timing
    def transform(_, telecoms):

        def scrub_number(telecom):
            telecom_id, system, value, *rest = telecom
            return (telecom_id, system, synthetic_number(value), *rest)

        res = Just(list(map(scrub_number, telecoms)))

        return res

    return SQLJob(extract_stmt, transform, load_stmt).run(connections)
