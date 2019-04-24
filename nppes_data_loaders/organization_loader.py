import toolz

from nppes_data_generators.names.organizations import synthetic_org_name_generator
from nppes_data_generators.npis.npi import synthetic_npi_generator
from nppes_data_loaders.connections import connection, query


def migrate_organizations(from_, to):
    from_cnx = connection(from_)
    from_cursor = from_cnx.cursor()

    to_cnx = connection(to)
    to_cursor = to_cnx.cursor()

    name_synthesizer = synthetic_org_name_generator()
    npis = synthetic_npi_generator()

    result = org_names_and_taxonomies(from_cursor)\
        .map(lambda res: map(lambda _: (_[0], name_synthesizer(*_[1:])), res))\
        .map(lambda res: map(lambda a, b: a + (b,), res, npis))\
        .map(lambda res: map(lambda _:((_[0], _[1]), (_[0], _[2])), res))\
        .map(lambda _: zip(*_))\
        .map(load_synthetic_organizations(to_cursor))\
        .map(list)

    if result.is_just():
        to_cnx.commit()
    else:
        to_cnx.rollback()

    to_cursor.close()
    to_cnx.close()

    from_cursor.close()
    from_cnx.close()


def org_names_and_taxonomies(cursor):
    stmt = """
            SELECT o.organization_id, o.name, COALESCE(GROUP_CONCAT(DISTINCT t.value), '') FROM vhdir_organization o
            LEFT JOIN organization_taxonomy t ON o.organization_id = t.organization_id
            GROUP BY o.organization_id
            ORDER BY name;
           """

    return query(cursor, stmt)


@toolz.curry
def load_synthetic_organizations(cursor, synthetic_data):
    id_and_name, id_and_npi = synthetic_data
    stmt1 = 'INSERT INTO vhdir_organization (organization_id, name) VALUES (%s, %s);'
    stmt2 = "INSERT INTO identifier (`use`, system, organization_id, value) " \
            "VALUES ('official', 'http://hl7.org/fhir/sid/us-npi', %s, %s);"

    return query(cursor, stmt1, id_and_name)\
        .map(lambda c: query(c, stmt2, id_and_npi))


def pull_organizations(cursor):
    # TODO: link to organization_contact when ready
    stmt = """ SELECT vo.organization_id, vo.name,
                       i.identifier_id,
                       t.telecom_id, t.value,
                       a.address_id, a.state
                FROM vhdir_organization vo
                LEFT JOIN identifier i ON vo.organization_id = i.organization_id
                LEFT JOIN telecom t ON vo.organization_id = t.organization_id
                LEFT JOIN address a ON vo.organization_id = a.organization_id
                WHERE i.system='http://hl7.org/fhir/sid/us-npi'
                AND t.system in ('phone', 'fax');
            """

    return query(cursor, stmt)


if __name__ == '__main__':
    migrate_organizations('spd_small', 'spd_small_scrubbed')


