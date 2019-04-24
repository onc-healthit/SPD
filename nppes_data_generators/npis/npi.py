import logging

from fn import F

from nppes_data_generators.npis.config import LOWER_LIMIT, UPPER_LIMIT, PREFIX
from nppes_data_generators.npis.utils import double_to_digit, backward_digit_generator, add, append_digit

''' We will start our NPIs with 999999999 and decrement
    Real NPIs start with 1 or 2 so we don't go lower than 300000000
    The algorithm works as such: (cf. https://www.cms.gov/Regulations-and-Guidance/Administrative-Simplification/NationalProvIdentStand/Downloads/NPIcheckdigit.pdf)
    Task #1. Double the value of alternate digits beginning with the rightmost digit.
    Task #2a. Add the individual digits of the products resulting from step 1 to the unaffected digits 
             from the original number.
    Task #2b. [...]However, in order that any NPI could be used as a card issuer identifier on a 
             standard health identification card, the check digit will always be calculated as if the prefix is present.
             This is accomplished by adding the constant 24 in step 2 of the check digit calculation.
    Task #3. Subtract the total obtained in step 2 from the next higher number ending in zero. This is the check digit. 
             If the total obtained in step 2 is a number ending in zero, the check digit is zero.
'''


def luhn_digit_generator(number):
    '''
    Given a number, produces its digits one at a time starting from the right
    doubling every other digit.
    :param number:
    :return:
    '''
    return (double_to_digit(d) if i % 2 == 0 else d for
            (i, d) in enumerate(backward_digit_generator(number)))


def calculate_check_digit(n):
    '''
    0 if last digit is 0 else diff with the next higher multiple of 10
    :param n:
    :return:
    '''
    mod_10 = n % 10
    return mod_10 if mod_10 == 0 else 10 - (n % 10)


def npi(number):
    '''
    Calculates the NPI for a given number
    '''
    pipeline = (
            F() >> luhn_digit_generator     # Task #1
                >> sum                      # Task #2a
                >> add(PREFIX)              # Task #2b
                >> calculate_check_digit    # Task #3
                >> append_digit(number)     # Append the check digit to the given number => NPI
    )
    return pipeline(number)


def synthetic_npi_generator(upper_bound=UPPER_LIMIT, lower_bound=LOWER_LIMIT):
    '''
    Generates all NPIs in a given range going backward i.e. from the upper_bound down to the lower_bound.
    :param upper_bound:
    :param lower_bound:
    :return:
    '''
    logger = logging.getLogger('npi_generator')

    if upper_bound > UPPER_LIMIT:
        upper_bound = UPPER_LIMIT
        logger.warning("Upper bound to high. Will start at %.0f instead." % UPPER_LIMIT)

    if lower_bound < LOWER_LIMIT:
        lower_bound = LOWER_LIMIT
        logger.warning("Lower bound to low. Will start at %.0f instead." % LOWER_LIMIT)

    return map(npi, range(upper_bound, lower_bound, -1))
