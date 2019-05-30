import os
import mysql.connector
from oslash import Just, Nothing


def connection(dbname, **kwargs):
    return mysql.connector.connect(
        host=os.environ.get('HOST'),
        user=os.environ.get('USER'),
        passwd=os.environ.get('PASSWD'),
        database=dbname,
        **kwargs
    )


def query(cursor, statement, params=None):
    try:
        if isinstance(params, (dict, list, tuple)):
            cursor.executemany(statement, params)
        elif params:
            cursor.execute(statement, params)
        else:
            cursor.execute(statement)
        return Just(cursor)
    except mysql.connector.Error as e:
        print(e)
        cursor.close()
        return Nothing()
