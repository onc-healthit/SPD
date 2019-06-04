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
    '''
    Execute a SQL query using the given cursor.

    If params is a collection, will execute queries batching by 10K to avoid transaction rejection.

    :param cursor: SQL cursor
    :param statement: Statement to execute
    :param params: SQL query parameters
    :return: Just(cursor) or Nothing() if query failed
    '''
    try:
        if isinstance(params, (dict, list, tuple)):
            step = 10000
            for i in range(0, len(params), step):
                cursor.executemany(statement, params[i:i+step])
        elif params:
            cursor.execute(statement, params)
        else:
            cursor.execute(statement)
        return Just(cursor)
    except mysql.connector.Error as e:
        print(e)
        cursor.close()
        return Nothing()
