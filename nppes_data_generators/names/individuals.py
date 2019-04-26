import os

from toolz import memoize

from utils.common import load_csv
from utils.scrubbing import pick_other


def synthetic_first_name_generator():
    """
    The suffix generator is used to specify that the output is calculated on-demand as opposed to batching.

    :return: Memoized partial application of :pick_name
             Its signature is `str -> str`
    """
    vocabulary = os.path.join(os.path.dirname(__file__), '..', 'vocabulary', 'names')

    first_names = set(_[0] for _ in load_csv(os.path.join(vocabulary, 'first_names.csv')))

    return memoize(pick_other(first_names))


def synthetic_last_name_generator():
    """
    The suffix generator is used to specify that the output is calculated on-demand as opposed to batching.

    :return: Memoized partial application of :pick_name
             Its signature is `str -> str`
    """
    vocabulary = os.path.join(os.path.dirname(__file__), '..', 'vocabulary', 'names')

    last_name = set(_[0] for _ in load_csv(os.path.join(vocabulary, 'last_names.csv')))

    return memoize(pick_other(last_name))
