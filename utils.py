import time


def timing(f, *args):
   start = time.time()
   res = f(*args)
   stop = time.time()
   print(stop - start)
   return res
