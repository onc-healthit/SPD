import toolz


@toolz.memoize(key=lambda args, _: args[0])
def synthetic_network_name_generator(name, state, num):
    return 'Insurance Network Of {} #{}'.format(state, num) if name else name
