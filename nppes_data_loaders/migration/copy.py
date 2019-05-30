import toolz
from oslash import Just

from nppes_data_loaders.etl.etl import SQLJob


@toolz.curry
def copy(table, connections):
    from_, to = connections

    return SQLJob('',
                 lambda cursor: lambda params: Just(None),
                 'INSERT INTO {2}.{0} SELECT * FROM {1}.{0};'.format(table, from_.database, to.database))\
        .run(connections)
