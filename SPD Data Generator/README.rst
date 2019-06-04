SPD NPPES Data Generators
=========================


A set of modules to generate Synthetic Provider Data:

-  Addresses
-  Individual Names
-  Organization Names
-  National Provider Identifiers
-  Phone Numbers

These generators are meant to be mapped to the original data. See usages.

National Provider Identifiers
-----------------------------
Ref. https://www.cms.gov/Regulations-and-Guidance/Administrative-Simplification/NationalProvIdentStand/Downloads/NPIcheckdigit.pdf

**Usage**

.. code-block:: python

    from nppes_data_generator.npis.npi import npi_generator

    providers = [{'npi': 'XXXXXXXXXX', 'fn': 'Stan', 'ln': 'Marsh'},
                 {'npi': 'YYYYYYYYYY', 'fn': 'Kyle', 'ln': 'Broflowsky'},
                 {'npi': 'ZZZZZZZZZZ', 'fn': 'Kenny', 'ln': 'McCormick'}]
    synthetic_providers = map(lambda p, npi: {**p, **{'npi': npi}}, providers, npi_generator())

    assert list(synthetic_providers) == [{'npi': 9999999995, 'fn': 'Stan', 'ln': 'Marsh'},
                                         {'npi': 9999999987, 'fn': 'Kyle', 'ln': 'Broflowsky'},
                                         {'npi': 9999999979, 'fn': 'Kenny', 'ln': 'McCormick'}]