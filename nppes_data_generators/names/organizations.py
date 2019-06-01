import random

import toolz
from oslash import Just, List

from nppes_data_generators.utils.common import load_vocabulary
from nppes_data_generators.utils.scrubbing import re_remove, re_split, remove_unwanted, breakdown, pick_other, \
    strip_non_characters


@toolz.curry
def find_acronym(acronyms, token):
    """
    1. If the token does not contain a dot, return it else
    2. Remove all dots and check if it's a known acronym, if yes return the known acronym else
    3. Split into two tokens and return

    :param token:
    :return: A list of one token if it's an acronym or two tokens if it's not
    """
    no_dot = re_remove(r'\.', token)
    return List.unit(no_dot) if no_dot in acronyms else List.from_iterable(filter(len, re_split(r'\.', token)))


def scrub_token(non_identifiables, names, token):
    """

    :param non_identifiables: The vocabulary that is assumed to be non identifiable i.e. that we can keep
    :param names: A collection of known names from which we pick one if the token happens to be a name itself
    (we want a name to remain a name, but a different one)
    :param token: The token to scrub
    :return: The token itself if it's non-identifiable, a random name if it's a name, and an empty string if
    we don't know
    """
    return token if token in non_identifiables \
        else pick_other(names, token) if token in names \
        else ''


@toolz.curry
def scrub_list(non_identifiables, restricted_non_identifiables, names, token_list):
    """
    Some names aren't really made of any identifiable vocabulary, so we recursively give it one more try with an
    narrower collection of terms, and if we still can't scrub it we return an empty tuple.

    Example:
    'DERMATOLOGY CENTER' ->(1st pass) 'DERMATOLOGY CENTER' ->(2nd pass) 'DERMATOLOGY'
    :return: A tuple of synthesized tokens
    """
    scrubbed_list = tuple(_ for _ in (scrub_token(non_identifiables, names, _) for _ in token_list) if _)
    res = scrub_list(restricted_non_identifiables, [], names, token_list) if token_list and scrubbed_list == token_list \
        else scrubbed_list if all(map(lambda _: _ in names or _ in non_identifiables, scrubbed_list)) \
        else tuple()
    return res


@toolz.curry
def append_taxonomies(taxonomies, names, tokens):
    """
    Would skip and prepend between 0 and 1 random name if the tokens list is self-sufficient (i.e. at least 2 tokens),
    would append all taxonomies and prepend between 1 and 2 names otherwise.

    :param taxonomies:
    :param tokens:
    :return:
    """
    has_name = bool(names & set(tokens))
    res = (tokens, random.randint(0, 0 if has_name else 1)) if len(tokens) > 1 or not taxonomies \
        else (tokens + taxonomies, random.randint(1, 1 if has_name else 2))
    return res


@toolz.curry
def prepend_names(names, tokens_and_number_of_names):
    """
    Given a list of tokens and an number, will prepend this number of random names to the list of tokens.
    Provides some additional scrubbing.

    :param tokens_and_number_of_names: The tokens and the number of random names to prepend to these tokens
    :return:
    """
    tokens, number_of_names = tokens_and_number_of_names
    prefix = tuple(' & '.join(random.sample(names, number_of_names)).split())
    res = tuple(prefix + ('-',) + tokens if prefix and tokens else tokens or prefix)
    return res


@toolz.curry
def tokenize(acronyms, name):
    """
    Steps are:
    1. Remove unwanted punctuation (for instance we don't mind parenthesize or brackets or some other punctuation)
    2. Break the name down into a list of tokens
    3. Check if tokens that contain a dot are actually some known acronym or if we should simply split it in two tokens

    :param acronyms: A collection of known acronyms. Used to identify acronyms ignoring the dots
    (i.e. M.D = M.D. = MD. = MD)
    :param name: The organization name
    :return: A collection of tokens as a tuple. Must be a tuple as its used as a dict key for caching purpose.
    https://wiki.python.org/moin/DictionaryKeys
    """
    tokens = Just(name)\
        .map(remove_unwanted)\
        .map(breakdown)\
        .map(List.from_iterable)\
        .from_maybe(List.empty())\
        .bind(find_acronym(acronyms))
    return tuple(tokens)


@toolz.memoize(key=lambda args, _: args[-1])
def scrub_tokenized_name(non_identifiables, med_vocab, names, taxonomies, tokens):
    """
    Caches the result of scrubbing the given tokens to keep consistency i.e. some orgs may have the same name and be
    differentiated by other properties (NPI), we want them to keep the same synthetic name as well.

    We prefer to cache the token list instead of the name itself as punctuation might interfere i.e.
    'MY ORGANIZATION, INC.' = 'MY ORGANIZATION INC.' = 'MY ORGANIZATION INC'

    The scrubbing workflow is s such:
    1. Scrub the given tokens
    2. If the result isn't persuasive and there is taxonomies available, append them
    3. If the result is still not persuasive or to complete it, prepend some names

    :param tokens: The list of words found in the organization name (see :tokenize)
    :return: A new list of synthetic tokens
    """
    return Just(tokens)\
        .map(scrub_list(non_identifiables, med_vocab, names)) \
        .map(append_taxonomies(taxonomies, names)) \
        .map(prepend_names(names)) \
        .from_just()


@toolz.curry
def scrub_name(non_identifiables, med_vocab, names, acronyms, name, *taxonomies):
    """
    :param id: The organization id
    :param name: The organization name
    :param taxonomies: A list of taxonomies attached to this organization
    :return: A tuple (id, str) i.e. attach the synthetic name to the same id
    """
    @toolz.curry
    def token_scrubber(tokens):
        """
        Couldn't figure how to combine memoization and currying in :scrub_tokenized_name so I decomposed it.
        We could also simply use a lambda.
        """
        return scrub_tokenized_name(non_identifiables, med_vocab, names, taxonomies, tokens)

    return Just(name)\
        .map(strip_non_characters)\
        .map(tokenize(acronyms))\
        .map(token_scrubber)\
        .map(' '.join)\
        .from_just()


def synthetic_org_name_generator():
    """
    Set up context for :scrub_name
    In order to synthesize an organization name, we need to load some vocabulary to help the algo categorize
    the words found in a name i.e. names, medical_vocab, acronyms...

    :return: Partial :scrub_name with signature `str, list[str] -> str`
    """
    load_names_vocab = load_vocabulary('names')

    entity_types = load_names_vocab('entity_types.csv')
    first_names = load_names_vocab('first_names.csv')
    last_names = load_names_vocab('last_names.csv')
    med_vocab = load_names_vocab('medical_vocab.csv')
    name_prefixes = load_names_vocab('name_prefixes.csv')
    name_suffixes = load_names_vocab('name_suffixes.csv')
    titles = load_names_vocab('titles.csv')
    general_vocab = load_names_vocab('general_vocab.csv')

    names = first_names | last_names
    non_identifiables = entity_types | med_vocab | titles | general_vocab
    acronyms = entity_types | titles | name_prefixes | name_suffixes

    return scrub_name(non_identifiables, med_vocab, names, acronyms)
