from toolz import memoize

from nppes_data_generators.utils.common import load_vocabulary
from nppes_data_generators.utils.scrubbing import pick_other


def synthetic_first_name_generator():
    """
    The suffix generator is used to specify that the output is calculated on-demand as opposed to batching.

    :return: Memoized partial application of :pick_name
             Its signature is `str -> str`
    """

    female_first_names = load_vocabulary('names', 'female_first_names.csv')

    male_first_names = load_vocabulary('names', 'male_first_names.csv')

    both = female_first_names & male_first_names

    return memoize(
        lambda name: pick_other(both if name in both else
                                female_first_names if name in female_first_names else
                                male_first_names,
                                name)
    )


def synthetic_last_name_generator():
    """
    The suffix generator is used to specify that the output is calculated on-demand as opposed to batching.

    :return: Memoized partial application of :pick_name
             Its signature is `str -> str`
    """

    last_names = load_vocabulary('names', 'last_names.csv')

    return memoize(pick_other(last_names))
