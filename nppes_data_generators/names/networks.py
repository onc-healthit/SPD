import toolz


@toolz.memoize(key=lambda args, _: args[0])
def synthetic_network_name_generator(name, state, num):
    '''
    Make up an synthetic Insurance Network name concatenating a state and a number

    :param name: Name to be memoized to speed up future evaluation
    :param state: The state to attach to the synthetic name
    :param num: The num to attach to the synthetic name
    :return: synthetic name as string
    '''
    return 'Insurance Network Of {} #{}'.format(state, num) if name else name
