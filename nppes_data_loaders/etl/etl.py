import logging
from functools import reduce

from oslash import Just

from nppes_data_loaders.connections import query, connection


class SPDETL:

    def __init__(self, *pipelines):
        self.pipelines = pipelines

    def run(self, from_, to):
        from_cnx = connection(from_)
        to_cnx = connection(to)

        try:
            migration = reduce(lambda acc, pipeline: acc | pipeline, self.pipelines, Just((from_cnx, to_cnx)))

            if migration.is_just:
                to_cnx.commit()
                print("Migration complete.")
            else:
                print("Migration failed.")
        except Exception as e:
            logger = logging.getLogger()
            handler = logging.StreamHandler()
            formatter = logging.Formatter('%(asctime)s %(name)-8s %(levelname)-8s [%(filename)s:%(lineno)d] %(message)s')
            handler.setFormatter(formatter)
            logger.addHandler(handler)
            logger.exception(e)
        finally:
            print('Closing all')
            to_cnx.rollback()
            to_cnx.close()
            from_cnx.close()


class SQLPipeline:

    def __init__(self, *jobs, setup=None, teardown=None):
        self.jobs = jobs
        self.setup = setup
        self.teardown = teardown

    def run(self, connections):
        from_cnx, to_cnx = connections

        from_cursor = from_cnx.cursor()
        to_cursor = to_cnx.cursor()

        init = query(to_cursor, self.setup).map(lambda _: (from_cursor, _)) if self.setup else \
            Just((from_cursor, to_cursor))

        return reduce(lambda acc, m: acc | m, self.jobs, init) \
               | (lambda _: query(to_cursor, self.teardown) if self.teardown else Just(_)) \
               | (lambda _: Just(connections))


class SQLJob:

    def __init__(self, extract_stmt, transform, load_stmt):
        self.extract_stmt = extract_stmt
        self.transform = transform
        self.load_stmt = load_stmt

    def __call__(self, cursors):
        from_cursor, to_cursor = cursors

        return query(from_cursor, self.extract_stmt) \
               | self.transform(to_cursor) \
               | (lambda ts: query(to_cursor, self.load_stmt, tuple(ts) if ts else ts)) \
               | (lambda tc: Just((from_cursor, tc)))

    def run(self, connections):
        return SQLPipeline(self).run(connections)
