import itertools

import toolz
from oslash import Just
from toolz import first

from nppes_data_generators.npis.npi import synthetic_npi_generator
from nppes_data_loaders.connections import query
from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def npi_transformer(entity_id, to_cursor, records):
    '''
    Use by :func:migrate_identifier

    Generate synthesized NPIs in reversed order starting at the minimum NPI value already in the DB.

    :param entity_id: The entity for which to synthesize identifiers
    :param to_cursor: MySQL cursor. Helps in getting min NPI already in DB.
    :param records: Records for which to synthesize identifier values
    :return: Just(synthesized records) or Nothing() in case of any failure
    '''
    stmt = """SELECT CAST(MIN(value) AS UNSIGNED) 
              FROM identifier 
              WHERE {} IS NOT NULL 
              AND system='http://hl7.org/fhir/sid/us-npi';""".format(entity_id)
    return query(to_cursor, stmt) \
        .map(next) \
        .map(first) \
        .map(lambda min_npi: synthetic_npi_generator(upper_bound=min_npi // 10 - 1) if min_npi
            else synthetic_npi_generator()) \
        .map(lambda npis: map(lambda r, npi: r[:-1] + (npi,), records, npis))


@toolz.curry
def hios_issuer_id_transformer(entity_id, to_cursor, records):
    '''
    Use by :func:migrate_identifier

    Generate identifier values incrementally based on the maximum value already in the DB.

    :param entity_id: The entity for which to synthesize identifiers
    :param to_cursor: MySQL cursor. Helps in getting max value already in DB.
    :param records: Records for which to synthesize identifier values
    :return: Just(synthesized records) or Nothing() in case of any failure
    '''
    stmt = """SELECT CAST(MAX(value) AS UNSIGNED) 
              FROM identifier 
              WHERE {} IS NOT NULL 
              AND system='https://www.cms.gov/CCIIO/';""".format(entity_id)

    return query(to_cursor, stmt)\
        .map(next)\
        .map(first)\
        .map(lambda max_issuer_id: itertools.count((max_issuer_id or 0)+1))\
        .map(lambda issuer_ids: map(lambda record, issuer_id: record[:-1] + (issuer_id,), records, issuer_ids))


@toolz.curry
def identity_transformer(entity_id, to_cursor, records):
    '''
    Use by :func:migrate_identifier

    Return the identifiers unchanged.

    :param entity_id: Ignored
    :param to_cursor: Ignored
    :param records: Records for which to synthesize identifier values
    :return: Just(records)
    '''
    return Just(records)


@toolz.curry
def migrate_identifier(entity_id, system, transformer):
    '''
    Migrate the identifier table for the given entity and system. This function is meant to be called by the entity
    for which we want to synthesize identifiers.

    :param entity_id: Entity to migrate identifiers for (ex. vhdir_organization, vhdir_practitioner...)
    :param system: Identifier system use for these identifiers (ex. http://hl7.org/fhir/sid/us-npi)
    :param transformer: Not all identifiers are synthesized the same way depending on it's context. The entity calling
    this method will tell which one to use.
    :return: Just(connections) if everything went well else Nothing()
    '''
    extract_stmt = """
                    SELECT {0}, identifier_id, identifier_status, identifier_status_value_code, `use`, system, 
                        type_cc_id, period_start, period_end, value
                    FROM identifier 
                    WHERE {0} IS NOT NULL 
                    AND system='{1}';
                    """.format(entity_id, system)
    load_stmt = """
                INSERT INTO identifier ({}, identifier_id, identifier_status, identifier_status_value_code, `use`, 
                    system, type_cc_id, period_start, period_end, value) 
                VALUES ({});
                """.format(entity_id, ('%s,'*10)[:-1])

    return SQLJob(extract_stmt, transformer(entity_id), load_stmt)
