import csv

import toolz
from oslash import Nothing
from toolz.curried import identity

from nppes_data_loaders.connections import connection, query


@toolz.curry
def get_nppes_taxonomies(cursor, id_and_npi):
    fields = ('NPI',
              '`Healthcare Provider Taxonomy Code_1`',
              '`Healthcare Provider Taxonomy Code_2`',
              '`Healthcare Provider Taxonomy Code_3`',
              '`Healthcare Provider Taxonomy Code_4`',
              '`Healthcare Provider Taxonomy Code_5`',
              '`Healthcare Provider Taxonomy Code_6`',
              '`Healthcare Provider Taxonomy Code_7`',
              '`Healthcare Provider Taxonomy Code_8`',
              '`Healthcare Provider Taxonomy Code_9`',
              '`Healthcare Provider Taxonomy Code_10`',
              '`Healthcare Provider Taxonomy Code_11`',
              '`Healthcare Provider Taxonomy Code_12`',
              '`Healthcare Provider Taxonomy Code_13`',
              '`Healthcare Provider Taxonomy Code_14`',
              '`Healthcare Provider Taxonomy Code_15`')

    ids, npis = zip(*id_and_npi)

    stmt = 'SELECT {} FROM npi WHERE NPI IN ({}) ORDER BY NPI;'.format(','.join(fields), ','.join(npis))

    return query(cursor, stmt).map(lambda _: zip(ids, _))


def get_spd_npis(cursor):
    stmt = "SELECT organization_id, value " \
           "FROM identifier " \
           "WHERE organization_id IS NOT NULL AND system='http://hl7.org/fhir/sid/us-npi' " \
           "ORDER BY value;"

    return query(cursor, stmt)


@toolz.curry
def insert_org_taxonomies(cursor, triplets):
    stmt = "INSERT INTO organization_taxonomy (organization_id, code, value) VALUES (%s, %s, %s);"
    return query(cursor, stmt, triplets)


if __name__ == '__main__':
    #TODO: Clean all that up
    spd_cnx = connection('spd_small')
    spd_cursor = spd_cnx.cursor()

    nppes_cnx = connection('nppes')
    nppes_cursor = nppes_cnx.cursor()

    with open('../static/taxonomy_mappings.csv', newline='') as f:
        reader = csv.reader(f)
        taxonomy_dict = {_[0]: _[1] for _ in reader}


    def remove_npi_and_empty_taxs(id_npi_and_taxs):
        id, t = id_npi_and_taxs
        return zip((id,) * len(t), filter(identity, set(t[1:])))


    def attach_taxonomy_values(id_and_taxs):
        return map(lambda t: (t[0], t[1], taxonomy_dict.get(t[1])), id_and_taxs)


    def flatten(ll):
        return (tuple(_) for l in ll for _ in l)


    try:
        npis = get_spd_npis(spd_cursor)\
            .map(get_nppes_taxonomies(nppes_cursor)) \
            .from_maybe(Nothing()) \
            .map(lambda _: map(remove_npi_and_empty_taxs, _)) \
            .map(lambda _: map(attach_taxonomy_values, _)) \
            .map(flatten) \
            .map(list) \
            .map(insert_org_taxonomies(spd_cursor)) \
            .from_maybe(Nothing()) \
            .map(lambda _: 'Inserted {} rows'.format(_.rowcount)) \
            .from_maybe(None)
        if npis:
            spd_cnx.commit()
    except Exception as e:
        raise e
    finally:
        nppes_cursor.close()
        nppes_cnx.close()

        spd_cursor.close()
        spd_cnx.close()
