from oslash import Just

from nppes_data_loaders.etl.etl import SQLJob, SQLPipeline


def copy(table, setup=None, teardown=None):
    '''
    Copy the given table from on DB to another
    Optionally sets up and tears down the transaction

    :param table: Table to copy
    :param setup: Operation to perform before the transaction
    :param teardown: Operation to perform once the transaction is done
    :return: Just(connections) if everything went well else Nothing()
    '''

    def run(connections):
        from_, to = connections

        return SQLPipeline(
            SQLJob('',
                   lambda cursor, params: Just(None),
                   'INSERT INTO {2}.{0} SELECT * FROM {1}.{0};'.format(table, from_.database, to.database)),
            setup=setup,
            teardown=teardown
        ).run(connections)

    run.__name__ = 'copy ' + table

    return run
