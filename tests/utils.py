import time


def timing(f):
    '''
    Timing wrapper. Useful to test perfs.

    Example:
      def f(x, y):
         return x + y

      time_spent, res = timing(f)
      print('My function f ran in %s seconds' % time_spent)

    :param f: The function to time
    :param args: Its arguments
    :return:
    '''
    def timed(*args, **kwargs):
        start = time.time()
        res = f(*args, **kwargs)
        stop = time.time()
        time_spent = stop - start
        print('{} done in {}s'.format(f.__name__, time_spent))
        return res

    return timed


def verbose(f):

    def log(*args, **kwargs):
        res = f(*args, **kwargs)
        print('{} done.'.format(f.__name__))
        return res

    return log
