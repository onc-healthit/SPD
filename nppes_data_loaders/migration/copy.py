from functools import wraps

import toolz
from oslash import Just

from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline


def copy(table, setup=None, teardown=None):

    def run(connections):
        from_, to = connections

        return SQLPipeline(
            SQLJob('',
                   lambda cursor: lambda params: Just(None),
                   'INSERT INTO {2}.{0} SELECT * FROM {1}.{0};'.format(table, from_.database, to.database)),
            setup=setup,
            teardown=teardown
        ).run(connections)

    run.__name__ = 'copy ' + table

    return run
