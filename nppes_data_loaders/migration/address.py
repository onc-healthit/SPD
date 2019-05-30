import toolz
from oslash import Just

from nppes_data_generators.addresses.address_lines import synthetic_address_line_generator
from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def migrate_address(connections):
    extract_stmt = 'SELECT * FROM address;'
    load_stmt = 'INSERT INTO address VALUES ({});'.format(('%s,'*18)[:-1])

    @toolz.curry
    def transform(_, addresses):
        address_line_generator = synthetic_address_line_generator()

        def scrub_line1(record):
            address_id, use, type, text, line1, *rest = record
            return (address_id, use, type, text, address_line_generator(line1), *rest)

        return Just(map(scrub_line1, addresses))

    return SQLJob(extract_stmt, transform, load_stmt).run(connections)
