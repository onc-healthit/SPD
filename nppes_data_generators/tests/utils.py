import time


def timing(f, *args):
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
    start = time.time()
    res = f(*args)
    stop = time.time()
    time_spent = stop - start
    return time_spent, res
