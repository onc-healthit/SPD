from toolz import memoize

from nppes_data_generators.utils.common import load_vocabulary
from nppes_data_generators.utils.scrubbing import pick_one


def synthetic_address_line_generator():
    '''
    Generate an address line concatenating a number, a cardinal direction, a street name and a street type.

    Memoizes the result for a given input to speedup the process.

    :return: Memoized function to be called with a given input
    '''
    load_addr_vocab = load_vocabulary('addresses')

    cardinal_directions = load_addr_vocab('cardinal_directions.csv')
    street_types = load_addr_vocab('street_types.csv')
    street_names = load_addr_vocab('street_names.csv')

    return memoize(lambda original: ' '.join(address_cmp for address_cmp in (str(pick_one(range(1, 10000))),
                                                                             pick_one(cardinal_directions | {''}),
                                                                             pick_one(street_names),
                                                                             pick_one(street_types))
                                             if address_cmp))
