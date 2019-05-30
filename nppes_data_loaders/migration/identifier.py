import itertools

import toolz
from oslash import Just
from toolz import first

from nppes_data_generators.npis.npi import synthetic_npi_generator
from nppes_data_loaders.connections import query
from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def npi_transformer(entity_id, to_cursor, records):
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
def hios_issuer_id_transformer(entity_id, to_cursor, ids):
    stmt = """SELECT CAST(MAX(value) AS UNSIGNED) 
              FROM identifier 
              WHERE {} IS NOT NULL 
              AND system='https://www.cms.gov/CCIIO/';""".format(entity_id)

    return query(to_cursor, stmt)\
        .map(next)\
        .map(first)\
        .map(lambda max_issuer_id: itertools.count((max_issuer_id or 0)+1))\
        .map(lambda issuer_ids: map(lambda id, issuer_id: id[:-1] + (issuer_id,), ids, issuer_ids))


@toolz.curry
def identity_transformer(entity_id, to_cursor, ids):
    return Just(ids)


@toolz.curry
def migrate_identifier(entity_id, system, transformer):
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
