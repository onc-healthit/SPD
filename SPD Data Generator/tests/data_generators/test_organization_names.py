import unittest


class TestOrganizationNameScrubbing(unittest.TestCase):

    def test_find_acronym(self):
        pass

    def test_scrub_token(self):
        pass

    def test_scrub_list(self):
        pass

    def test_append_taxonomies(self):
        pass

    def test_prepend_names(self):
        pass

    def test_tokenize(self):
        pass

    def test_scrub_tokenized_name(self):
        pass

    def test_scrub_name(self):
        pass

    def test_synthetic_org_name_generator(self):
        orgs = [('CUMBERLAND COUNTY HOSPITAL SYSTEM, INC', 'TAX1'),
             ('COLLABRIA CARE', 'TAX2', 'TAX2', 'TAX4'),
             ('COLLABRIA, CARE.', 'TAX2', 'TAX4'),
             ('ADVANTAGE HOME HEALTH CARE, INC.',),
             ('PEKIN MRI, LLC', 'TAX1'),
             ('EASTERN STAR MASONIC HOME', 'TAX1', 'TAX3'),
             ('ADR LLC', 'TAX18', 'TAX1', 'TAX12', 'TAX6', 'TAX9'),
             ('AZAR/FILIPOV MD PA',),
             ('OAKVIEW MEDICAL CARE FACILITY', 'TAX1', 'TAX1', 'TAX1', 'TAX1', 'TAX1'),
             ('ADIRONDACK MEDICAL HEALTH CARE ASSOCIATES PLLC', 'TAX6'),
             ('ALABAMA CARDIOVASCULAR GROUP, P.C.', 'TAX8', 'TAX8')]
        pass
