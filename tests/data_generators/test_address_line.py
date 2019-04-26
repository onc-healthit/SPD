import unittest

from nppes_data_generators.addresses.address_lines import synthetic_address_line_generator, cardinal_directions, \
    street_types


class TestAddressLineGenerator(unittest.TestCase):

    def test_synthetic_address_line_generator(self):
        g = synthetic_address_line_generator()
        address_lines = (g('some address line {}'.format(i)) for i in range(10000))
        for address_line in address_lines:
            self.assertRegex(address_line, r'^[0-9]{{1,4}}\s(({})\s)?([\w|-]+\s)+({})$'.format('|'.join(cardinal_directions),
                                                                                               '|'.join(street_types)))
