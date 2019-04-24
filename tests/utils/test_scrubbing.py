import unittest

from utils.scrubbing import remove_unwanted, strip_non_characters, breakdown, pick_other


class TestScrubbingUtils(unittest.TestCase):

    def test_remove_extra_punct(self):
        self.assertEqual('', remove_unwanted('()|[]<>%*$!+`'))
        self.assertEqual('Some - other _ Input.', remove_unwanted('Some - other _ Input.'))

    def test_strip_non_characters(self):
        self.assertEqual('MY SENTENCE GOES HERE', strip_non_characters('.+_- 123 MY SENTENCE GOES HERE 456 !?,'))

    def test_breakdown(self):
        input = """Let's say that we have a sentence. First we'll turn it upper case, then we'll break it down and see 
        if we get the expected token list.""".upper()
        expected = ['LET', 'S', 'SAY', 'THAT', 'WE', 'HAVE', 'A', 'SENTENCE', 'FIRST', 'WE', 'LL', 'TURN', 'IT',
                    'UPPER', 'CASE', 'THEN', 'WE', 'LL', 'BREAK', 'IT', 'DOWN', 'AND', 'SEE', 'IF', 'WE', 'GET', 'THE',
                    'EXPECTED', 'TOKEN', 'LIST']
        self.assertListEqual(expected, list(breakdown(input)))

    def test_pick_other(self):
        picker = pick_other([1, 2])
        self.assertListEqual([2], list(set(picker(1) for _ in range(10000))))

        picker = pick_other([1, 1])
        self.assertListEqual([1], list(set(picker(1) for _ in range(10000))))
