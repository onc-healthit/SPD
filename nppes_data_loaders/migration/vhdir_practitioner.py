import toolz
from oslash import Just

from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline
from nppes_data_loaders.migration.identifier import migrate_identifier, npi_transformer

'''
Within the same transaction:
1. Migrate vhdir_practitioner: practitioner_id, active, gender, birthDate
2. Migrate identifier
3. Migrate telecom
4. Migrate address
5. Migrate name
'''
def migrate_vhdir_practitioner():
    extract_stmt = """
        SELECT practitioner_id, active, gender, birthDate FROM vhdir_practitioner;
        """
    load_stmt = """
        INSERT INTO vhdir_practitioner (practitioner_id, active, gender, birthDate)
                VALUES (%s, %s, %s, %s);
        """

    @toolz.curry
    def transform(_, pracs):

        def scrub_prac(prac):
            practitioner_id, active, gender, birthDate = prac
            return practitioner_id, active, gender, birthDate

        return Just(map(scrub_prac, pracs))

    return SQLJob(extract_stmt, transform, load_stmt)


def migrate_practitioners(connections):
    return SQLPipeline(migrate_vhdir_practitioner(),
                       migrate_identifier('practitioner_id', 'http://hl7.org/fhir/sid/us-npi', npi_transformer))\
        .run(connections)
