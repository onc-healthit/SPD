from functools import reduce

from oslash import Just

from nppes_data_loaders.connections import query, connection
from tests.utils import verbose


class SPDETL:
    '''
    Complete Extract-Transform_load process i.e. chaning SQLPipelines
    '''
    def __init__(self, *pipelines):
        self.pipelines = pipelines

    def run(self, from_, to):
        '''
        Reduces pipelines calling running them sequentially.
        Commit if success, log and rollback if not.

        Finally close everything.

        :param from_:
        :param to:
        :return:
        '''
        from_cnx = connection(from_)
        to_cnx = connection(to)

        try:
            migration = reduce(lambda acc, pipeline: acc | pipeline,
                               map(verbose, self.pipelines),
                               Just((from_cnx, to_cnx)))

            if migration.is_just:
                to_cnx.commit()
                print("Migration complete.")
            else:
                print("Migration failed.")
        except Exception as e:
            print(e)
        finally:
            print('Closing all')
            to_cnx.rollback()
            to_cnx.close()
            from_cnx.close()


class SQLPipeline:
    '''
    Execute SQLJobs sequentially
    '''

    def __init__(self, *jobs, setup=None, teardown=None):
        self.jobs = jobs
        self.setup = setup
        self.teardown = teardown

    def run(self, connections):
        '''
        Reduces jobs calling them sequentially. Optionally perform operations before and afterwards (setup/teardown)

        :param connections: Two-connection tuple to know where to pull the data from and here to push it back to
        :return:  Just(connections) if everything went well else Nothing()
        '''
        from_cnx, to_cnx = connections

        from_cursor = from_cnx.cursor()
        to_cursor = to_cnx.cursor()

        init = query(to_cursor, self.setup).map(lambda _: (from_cursor, _)) if self.setup else \
            Just((from_cursor, to_cursor))

        def reducer(acc, m):
            '''
            Commit each job

            :param acc: Accumulated result
            :param m: New job
            :return: Just(cursors) or Nothing() if job failed
            '''
            to_cnx.commit()
            return acc | m

        return reduce(reducer, self.jobs, init) \
               | (lambda _: query(to_cursor, self.teardown) if self.teardown else Just(_)) \
               | (lambda _: Just(connections))


class SQLJob:
    '''
    A SQL ETL job:
    '''
    def __init__(self, extract_stmt, transform, load_stmt):
        self.extract_stmt = extract_stmt
        self.transform = transform
        self.load_stmt = load_stmt

    def __call__(self, cursors):
        '''
        1. Extract executing extract_stmt using the first cursor
        2. Load results into memory. We might not do it here if we're able to highly rely on the network connection.
           i.e. for very big job the transaction would need to stay open all the way long if we don't buffer the result.
        3. Transform using the given transformer
        4. Load executing load_stmt using the second cursor and the transformed records

        :param cursors: Two-tuple of SQL cursors to execute the given statements
        :return: Just(cursors) or Nothing() in case of failure
        '''
        from_cursor, to_cursor = cursors

        return query(from_cursor, self.extract_stmt) \
               | (lambda fc: self.transform(to_cursor, tuple(fc))) \
               | (lambda ts: query(to_cursor, self.load_stmt, tuple(ts) if ts else ts)) \
               | (lambda tc: Just((from_cursor, tc)))

    def run(self, connections):
        '''
        Allow to run a job outside of a pipeline.

        :param connections: Two-connection tuple to know where to pull the data from and here to push it back to
        :return:  Just(connections) if everything went well else Nothing()
        '''
        return SQLPipeline(self).run(connections)
