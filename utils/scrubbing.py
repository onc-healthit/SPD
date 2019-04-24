import random
import re
from itertools import dropwhile

import toolz


def re_remove(pattern, input):
    return re.sub(pattern, '', input)


def re_strip(l_pattern, r_pattern, input):
    return re.sub(r_pattern, r'\1', re.sub(l_pattern, r'\1', input))


def re_split(pattern, input):
    return re.split(pattern, input)


@toolz.curry
def remove_unwanted(input):
    """
    For the purpose of scrubbing, there is some specific characters that we want to ignore so we simply remove them.

    :param input:
    :return:
    """
    unwanted = {'\(', '\)', '\|', '\[', '\]', '\<', '\>', '\%', '\*', '\$', '\!', '\+', '\`'}
    return re_remove(r'({})'.format('|'.join(unwanted)), input)


@toolz.curry
def strip_non_characters(input):
    """
    Remove extraneous unwanted characters at the beginning and at the end of a string.

    :param input:
    :return:
    """
    return re_strip(r'^(.+[A-Z])[^A-Z]+$', r'^[^A-Z]+(.+)', input)


@toolz.curry
def breakdown(input):
    """
    Break a string down into tokens, splitting on everything that's not an uppercase character or a dot, or on a dot
    that's not a word boundary i.e. the token is not considered to be a potential acronym.
    :param input:
    :return:
    """
    return filter(len, re_split(r'(?:[^A-Z\.])|(?:\.(?!\b))', input))


@toolz.curry
def pick_other(population, given):
    """
    Randomly pick a different name that the one given in input and cache it so that the same input will give the same
    output for the lifecycle of the function.

    random.sample returns a list of `unique` elements so it's sufficient to pick 2, if one is the same as the given
    simply return the other one. If the population does not provide an alternative,  return given itself.

    :param population: The names from which ti pick one.
    :param name: The name to be replaced.
    :return: A string representing a single name picked from the population.
    """
    try:
        return next(dropwhile(lambda _: _ == given, (_ for _ in random.sample(population, 2))))
    except StopIteration:
        return given
