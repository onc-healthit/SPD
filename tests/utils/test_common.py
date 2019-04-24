import unittest

from utils.common import cached


class TestCommonUtils(unittest.TestCase):

    def test_cached(self):

        @cached()
        def f(x):
            return x

        f(1)

        self.assertDictEqual(f.__closure__[0].cell_contents.cache, {(1,): 1})

    def test_cached_with_key_function(self):

        @cached(key=lambda _: _[-1])
        def f(x, y):
            return x + y

        f(1, 2)

        self.assertDictEqual(f.__closure__[0].cell_contents.cache, {2: 3})
