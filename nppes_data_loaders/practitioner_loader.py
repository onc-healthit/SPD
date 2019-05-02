import toolz

from nppes_data_generators.addresses.address_lines import synthetic_address_line_generator
from nppes_data_generators.names.organizations import synthetic_org_name_generator
from nppes_data_generators.names.individuals import synthetic_first_name_generator
from nppes_data_generators.names.individuals import synthetic_last_name_generator
from nppes_data_generators.npis.npi import synthetic_npi_generator
from nppes_data_generators.phone_numbers.phone_number import synthetic_number
from nppes_data_loaders.connections import connection, query


'''
Within the same transaction:
1. Migrate vhdir_organization: organization_id, name, active, partOf_organization_id
2. Update vhdir_organization: organization_id, partOf_organization_name
3. Migrate identifier: use, system, organization_id, value
4. Migrate telecom: organization_id, system, value
5. Migrate address: organization_id, use, type, line1, city, district, state, postalCode, country, latitude, longitude
6. Migrate organization_contact: organization_id, purpose_cc_id, name_id, address_id
'''
def migrate_vhdir_practitioner(from_cursor, to_cursor):
    extract_stmt = """
        SELECT practitioner_id, active, gender, birthDate FROM vhdir_practitioner;
        """
    load_stmt = """
        INSERT INTO vhdir_practitioner (practitioner_id, active, gender, birthDate)
                VALUES (%s, %s, %s, %s);
        """

    def transform(pracs):
        def scrub_prac(prac):
            practitioner_id, active, gender, birthDate = prac
            return practitioner_id, active, gender, birthDate
        return map(scrub_prac, pracs)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))

@toolz.curry
def migrate_identifier(from_cursor, to_cursor):
    extract_stmt = """
                    SELECT practitioner_id FROM vhdir_practitioner;
                    """
    load_stmt = """
                INSERT INTO identifier (`use`, system, practitioner_id, value) 
                VALUES ('official', 'http://hl7.org/fhir/sid/us-npi', %s, %s);
                """

    def transform(prac_ids):
        npis = synthetic_npi_generator()  # Should set upperbound depending on what's already loaded
        res = map(lambda id, npi: id + (npi,), prac_ids, npis)
        return res

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))

@toolz.curry
def migrate_telecom(from_cursor, to_cursor):
    extract_stmt = """
                    SELECT telecom_id, system, value, practitioner_id 
                    FROM telecom 
                    WHERE practitioner_id IS NOT NULL
                    AND system in ('phone', 'fax');
                    """
    load_stmt = 'INSERT INTO telecom (telecom_id, system, value, practitioner_id) VALUES (%s, %s, %s, %s);'

    def transform(telecoms):
        def scrub_number(telecom):
            telecom_id, system, value, practitioner_id = telecom
            return telecom_id, system, synthetic_number(value), practitioner_id
        return map(scrub_number, telecoms)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))

@toolz.curry
def migrate_address(from_cursor, to_cursor):
    extract_stmt = """
        SELECT address_id, `use`, type, text, line1, line2, city, district, state, postalCode, country,
        latitude, longitude, period_start, period_end, practitioner_id
        FROM address WHERE practitioner_id IS NOT NULL;
        """
    load_stmt = """
        INSERT INTO address (address_id, `use`, type, text, line1, line2, city, district, state, postalCode,
        country, latitude, longitude, period_start, period_end, practitioner_id)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
        """

    def transform(addresses):
        def scrub_line1(address):
            address_line_generator = synthetic_address_line_generator()
            address_id, use, type, text, line1, *rest = address
            return (address_id, use, type, text, address_line_generator(line1), *rest)
        return map(scrub_line1, addresses)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))

@toolz.curry
def migrate_names(from_cursor, to_cursor):
    extract_stmt = """
        SELECT name_id, `use`, text, family, given, prefix, suffix, period_start, period_end, practitioner_id
        FROM name WHERE practitioner_id IS NOT NULL;
        """
    load_stmt = """
        INSERT INTO name (name_id, `use`, text, family, given, prefix, suffix, period_start, period_end,
        practitioner_id)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
        """

    def transform(names):
        def scrub_name(name):
            first_name_generator = synthetic_first_name_generator()
            last_name_generator = synthetic_last_name_generator()
            name_id, use, text, family, given, *rest = name
            return (name_id, use, text, last_name_generator(family), first_name_generator(given), *rest)
        return map(scrub_name, names)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))

def migrate_practitioners(from_, to):
    from_cnx = connection(from_)
    from_cursor = from_cnx.cursor()

    to_cnx = connection(to)
    to_cursor = to_cnx.cursor()

    result = migrate_vhdir_practitioner(from_cursor, to_cursor) \
             | migrate_identifier(from_cursor) \
             | migrate_telecom(from_cursor) \
             | migrate_address(from_cursor) \
             | migrate_names(from_cursor)

    if result.is_just():
        to_cnx.commit()
    else:
        to_cnx.rollback()

    to_cursor.close()
    to_cnx.close()

    from_cursor.close()
    from_cnx.close()


if __name__ == '__main__':
    migrate_practitioners('spd_small', 'spd_small_scrubbed')
