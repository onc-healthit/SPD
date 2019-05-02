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
def migrate_vhdir_organization(from_cursor, to_cursor):
    extract_stmt = """
            SELECT o.organization_id, o.name, COALESCE(GROUP_CONCAT(DISTINCT t.value), ''), 
                active, partOf_organization_id
            FROM vhdir_organization o
            LEFT JOIN organization_taxonomy t ON o.organization_id = t.organization_id
            GROUP BY o.organization_id
            ORDER BY name;
           """
    load_stmt = """INSERT INTO vhdir_organization (organization_id, name, active, partOf_organization_id)
                VALUES (%s, %s, %s, %s);"""

    def transform(orgs):
        def scrub_org(org):
            name_synthesizer = synthetic_org_name_generator()
            organization_id, name, taxonomies, active, partOf_organization_id = org
            return organization_id, name_synthesizer(name, *taxonomies.split(',')), active, partOf_organization_id
        return map(scrub_org, orgs)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))


@toolz.curry
def update_vhdir_organization_partOf(from_cursor, to_cursor):
    extract_stmt = """
            SELECT o1.organization_id, o2.name 
            FROM vhdir_organization o1
            LEFT JOIN
            vhdir_organization o2
            ON o1.organization_id = o2.partOf_organization_id;
           """
    load_stmt = 'UPDATE vhdir_organization SET partOf_organization_name=%s WHERE organization_id=%s;'

    return query(from_cursor, extract_stmt)\
        .bind(lambda orgs: query(to_cursor, load_stmt, tuple(orgs)))


@toolz.curry
def migrate_identifier(from_cursor, to_cursor):
    extract_stmt = """
                    SELECT organization_id  FROM vhdir_organization;
                    """
    load_stmt = """
                INSERT INTO identifier (`use`, system, organization_id, value) 
                VALUES ('official', 'http://hl7.org/fhir/sid/us-npi', %s, %s);
                """

    def transform(org_ids):
        npis = synthetic_npi_generator()  # Should set upperbound depending on what's already loaded
        res = map(lambda id, npi: id + (npi,), org_ids, npis)
        return res

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))


@toolz.curry
def migrate_contacts(from_cursor, to_cursor):
    extract_stmt = """
        SELECT organization_contact_id, purpose_id, name_id, address_id, organization_id FROM organization_contact;
        """
    load_stmt = """
        INSERT INTO organization_contact (organization_contact_id, purpose_id, name_id, address_id, organization_id)
                VALUES (%s, %s, %s, %s, %s);
        """

    def transform(contacts):
        def scrub_contact(contact):
            organization_contact_id, purpose_id, name_id, address_id, organization_id = contact
            return organization_contact_id, purpose_id, name_id, address_id, organization_id
        return map(scrub_contact, contacts)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))


@toolz.curry
def migrate_telecom(from_cursor, to_cursor):
    extract_stmt = """
                    SELECT telecom_id, system, value, organization_id 
                    FROM spd_small.telecom 
                    WHERE organization_id IS NOT NULL
                    AND system in ('phone', 'fax');
                    """
    load_stmt = 'INSERT INTO telecom (telecom_id, system, value, organization_id) VALUES (%s, %s, %s, %s);'

    def transform(telecoms):
        def scrub_number(telecom):
            telecom_id, system, value, organization_id = telecom
            return telecom_id, system, synthetic_number(value), organization_id
        return map(scrub_number, telecoms)

    return query(from_cursor, extract_stmt)\
        .map(transform)\
        .bind(lambda ts: query(to_cursor, load_stmt, tuple(ts)))


@toolz.curry
def migrate_address(from_cursor, to_cursor):
    extract_stmt = """
        SELECT address_id, `use`, type, text, line1, line2, city, district, state, postalCode, country,
        latitude, longitude, period_start, period_end, organization_id
        FROM address WHERE organization_id IS NOT NULL;
        """
    load_stmt = """
        INSERT INTO address (address_id, `use`, type, text, line1, line2, city, district, state, postalCode,
        country, latitude, longitude, period_start, period_end, organization_id)
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
def migrate_contact_names(from_cursor, to_cursor):
    extract_stmt = """
        SELECT DISTINCT(name.name_id), `use`, text, family, given, prefix, suffix, period_start, period_end
        FROM name, organization_contact
        WHERE name.name_id = organization_contact.name_id;
        """
    load_stmt = """
        INSERT INTO name (name_id, `use`, text, family, given, prefix, suffix, period_start, period_end)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s);
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


def migrate_organizations(from_, to):
    from_cnx = connection(from_)
    from_cursor = from_cnx.cursor()

    to_cnx = connection(to)
    to_cursor = to_cnx.cursor()

    result = migrate_vhdir_organization(from_cursor, to_cursor) \
             | update_vhdir_organization_partOf(from_cursor) \
             | migrate_identifier(from_cursor) \
             | migrate_telecom(from_cursor) \
             | migrate_address(from_cursor) \
             | migrate_contact_names(from_cursor) \
             | migrate_contacts(from_cursor)

    if result.is_just():
        to_cnx.commit()
    else:
        to_cnx.rollback()

    to_cursor.close()
    to_cnx.close()

    from_cursor.close()
    from_cnx.close()


if __name__ == '__main__':
    migrate_organizations('spd_small', 'spd_small_scrubbed')