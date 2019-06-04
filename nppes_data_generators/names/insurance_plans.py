import re

import toolz

from nppes_data_generators.utils.common import load_vocabulary


@toolz.curry
def scrub(vocab):
    '''
    Word-tokenize the given name to keep some relevant vocabulary while providing a synthetic name.

    :param vocab: Known vocab to retain
    :return: Memoized function to be called with a given input
    '''
    @toolz.memoize
    def scrub_name(idx, name):
        suffix, n = re.subn(r'.*\s([0-9]+\*?(\/[0-9]+%?)+).*', r'\1', name)
        to_keep = ' '.join([_ for _ in vocab if _ in name.upper()] + ['Insurance Plan'])
        return '{}{}{}'.format(to_keep,
                               ', {}'.format(suffix) if n else '',
                               ' #{}'.format(idx) if to_keep == 'Insurance Plan' else '')
    return scrub_name


def synthetic_insurance_plan_name_generator():
    '''
    Load vocab and return function.

    :return: :func:scrub
    '''
    load_names_vocab = load_vocabulary('names')

    insurance_plans_vocab = load_names_vocab('insurance_plans.csv')

    return scrub(insurance_plans_vocab)
