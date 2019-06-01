from nppes_data_loaders.etl.etl import SPDETL
from nppes_data_loaders.migration.address import migrate_address
from nppes_data_loaders.migration.copy import copy
from nppes_data_loaders.migration.name import migrate_name
from nppes_data_loaders.migration.telecom import migrate_telecom
from nppes_data_loaders.migration.organization_alias import migrate_organization_alias
from nppes_data_loaders.migration.vhdir_insurance_plan import migrate_insurance_plans
from nppes_data_loaders.migration.vhdir_network import migrate_networks
from nppes_data_loaders.migration.vhdir_organization import migrate_organizations
from nppes_data_loaders.migration.vhdir_practitioner import migrate_practitioners
from tests.utils import timing

if __name__ == '__main__':
    from_ = 'spd_medium'
    to = 'spd_medium_scrubbed'

    pipeline = SPDETL(migrate_organizations,
                      migrate_organization_alias,
                      copy('vhdir_careteam'),
                      migrate_practitioners,
                      migrate_networks,
                      migrate_insurance_plans,
                      copy('resource_reference'),
                      migrate_name,
                      copy('contact'),
                      copy('vhdir_practitioner_role'),
                      migrate_telecom,
                      migrate_address,
                      copy('actor'),
                      copy('attestation'),
                      copy('care_team_alias'),
                      copy('fhir_codeable_concept'),
                      copy('npi_taxonomy'),
                      copy('participant'),
                      copy('primary_source'),
                      copy('provison'),
                      copy('qualification'),
                      copy('vhdir_healthcare_service'),
                      copy('vhdir_location'),
                      copy('vhdir_organization_affiliation'),
                      copy('vhdir_restriction'),
                      copy('vhdir_validation'))

    @timing
    def go():
        return pipeline.run(from_, to)
