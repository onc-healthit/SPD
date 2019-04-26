import os

from toolz import memoize

from utils.common import load_csv
from utils.scrubbing import pick_one


def _load(file):
    vocabulary = os.path.join(os.path.dirname(__file__), '..', 'vocabulary', 'addresses')
    return set(_[0] for _ in load_csv(os.path.join(vocabulary, file)))


cardinal_directions = _load('cardinal_directions.csv')
street_types = _load('street_types.csv')
street_names = _load('street_names.csv')


def synthetic_address_line_generator():

    return memoize(lambda original: ' '.join(address_cmp for address_cmp in (str(pick_one(range(1, 10000))),
                                                                             pick_one(cardinal_directions | {''}),
                                                                             pick_one(street_names),
                                                                             pick_one(street_types))
                                             if address_cmp))
