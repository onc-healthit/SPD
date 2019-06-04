from nppes_data_loaders.etl.etl import SPDETL
from nppes_data_loaders.migration.address import migrate_address
from nppes_data_loaders.migration.copy import copy
from nppes_data_loaders.migration.name import migrate_name
from nppes_data_loaders.migration.organization_alias import migrate_organization_alias
from nppes_data_loaders.migration.telecom import migrate_telecom
from nppes_data_loaders.migration.vhdir_insurance_plan import migrate_insurance_plans
from nppes_data_loaders.migration.vhdir_network import migrate_networks
from nppes_data_loaders.migration.vhdir_organization import migrate_organizations
from nppes_data_loaders.migration.vhdir_practitioner import migrate_practitioners

if __name__ == '__main__':
    from_ = 'spd_large'
    to = 'spd_large_scrubbed'

    SPDETL(migrate_organizations,
           migrate_organization_alias,
           copy('vhdir_careteam'),
           migrate_practitioners,
           migrate_networks,
           migrate_insurance_plans,
           copy('resource_reference', 'SET FOREIGN_KEY_CHECKS=0;', 'SET FOREIGN_KEY_CHECKS=1;'),
           migrate_name,
           copy('contact'),
           copy('vhdir_practitioner_role'),
           migrate_telecom,
           migrate_address,
           copy('actor'),
           copy('fhir_codeable_concept', 'SET FOREIGN_KEY_CHECKS=0;', 'SET FOREIGN_KEY_CHECKS=1;'),
           copy('attestation'),
           copy('care_team_alias'),
           copy('npi_taxonomy'),
           copy('participant'),
           copy('vhdir_validation'),
           copy('primary_source'),
           copy('provision'),
           copy('qualification'),
           copy('vhdir_healthcare_service'),
           copy('vhdir_location'),
           copy('vhdir_organization_affiliation'),
           copy('vhdir_restriction'),
           copy('organization_taxonomy'))\
        .run(from_, to)
