from nppes_data_loaders.migration.copy import copy
from nppes_data_loaders.migration.identifier import migrate_identifier, npi_transformer


def migrate_practitioners(connections):
    '''
    Copy practitioner then synthesize their identifiers.

    :param connections: Two-connection tuple to know where to pull the data from and here to push it back to
    :return: Just(connections) if everything went well else Nothing()
    '''
    return copy('vhdir_practitioner')(connections) \
           | migrate_identifier('practitioner_id', 'http://hl7.org/fhir/sid/us-npi', npi_transformer).run
