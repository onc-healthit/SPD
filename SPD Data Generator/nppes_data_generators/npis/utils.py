import operator

from fn.func import curried


def backward_digit_generator(number):
    '''
    Given a number, produces its digits one at a time starting from the right
    :param number:
    :return:
    '''
    if number == 0:
        return
    yield number % 10
    yield from backward_digit_generator(number // 10)


def double_to_digit(d):
    '''
    Doubles a digit. If the result is a 2-digit number subtract 9 i.e. sum thw 2 digits
    :param d:
    :return:
    '''
    assert 0 <= d <= 18, "Expected a positive 1 or 2-digit number maxed at 18. Got %.0f" % d
    dd = d * 2
    return dd if dd < 10 else dd - 9


@curried
def add(x, y):
    return operator.add(x, y)


@curried
def append_digit(number, digit):
    '''
    Appends a digit to a given number.
    :param number:
    :param digit:
    :return:
    '''
    return number * 10 + digit
