import toolz
from oslash import Just

from nppes_data_generators.names.individuals import synthetic_first_name_generator, synthetic_last_name_generator
from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def migrate_name(connections):
    extract_stmt = 'SELECT * FROM name;'
    load_stmt = 'INSERT INTO name VALUES ({});'.format(('%s,'*10)[:-1])

    @toolz.curry
    def transform(_, names):
        first_name_generator = synthetic_first_name_generator()
        last_name_generator = synthetic_last_name_generator()

        def scrub_name(record):
            name_id, use, text, family, given, *rest = record
            return (name_id, use, text, last_name_generator(family), first_name_generator(given), *rest)

        return Just(map(scrub_name, names))

    return SQLJob(extract_stmt, transform, load_stmt).run(connections)
