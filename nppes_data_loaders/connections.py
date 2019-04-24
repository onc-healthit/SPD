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
        try:
            iter(params)
            cursor.executemany(statement, params)
        except TypeError:
            cursor.execute(statement, params)
        return Just(cursor)
    except mysql.connector.Error as e:
        print(e)
        cursor.close()
        return Nothing()


def consume(cursor):
    try:
        next(cursor)
        return consume(cursor)
    except StopIteration:
        return
