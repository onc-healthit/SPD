import Sequelize from 'sequelize';
import { npidb, spddb, cciiodb } from '../util/dbconnect';
import _ from 'lodash';

const cciioModel = cciiodb.define ('network', {
	BusinessYear: { type: Sequelize.STRING }, 
	StateCode: { type: Sequelize.STRING }, 
	IssuerId: { type: Sequelize.INTEGER }, 
	SourceName: { type: Sequelize.STRING }, 
	ImportDate: { type: Sequelize.STRING },
	NetworkName: { type: Sequelize.STRING },
	NetworkId: { type: Sequelize.STRING }, 
	NetworkURL: { type: Sequelize.STRING }, 
	MarketCoverage: { type: Sequelize.STRING },
	DentalOnlyPlan: { type: Sequelize.STRING },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'network'	
});
cciioModel.removeAttribute('id');

const Cciio = cciiodb.models.network;

const insurancePlanModel = cciiodb.define('plan_attributes', {
	HIOSProductId: { type: Sequelize.INTEGER },
	IssuerId: { type: Sequelize.INTEGER },
	PlanId: { type: Sequelize.INTEGER },
	PlanMarketingName: { type: Sequelize.STRING },
	PlanType: { type: Sequelize.STRING },
	NetworkId: { type: Sequelize.INTEGER },
	ServiceAreaId: { type: Sequelize.INTEGER },
	MetalLevel: { type: Sequelize.STRING },
	PlanEffectiveDate: { type: Sequelize.DATE }, 
	PlanExpirationDate: { type: Sequelize.DATE },
},{
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'plan_attributes'	
});
insurancePlanModel.removeAttribute('id');

const InsurancePlan = cciiodb.models.plan_attributes;

const hiosModel = cciiodb.define ('HIOS', {
	HIOS_ISSUER_ID: { type: Sequelize.INTEGER }, 
	ISSR_LGL_NAME: { type: Sequelize.STRING }, 
	MarketingName: { type: Sequelize.STRING }, 
	State: { type: Sequelize.STRING }, 
	IndividualMarket: { type: Sequelize.STRING }, 
	SmallGroupMarket: { type: Sequelize.STRING }, 
	UnknownMarket: { type: Sequelize.STRING }, 
	LargeMarket: { type: Sequelize.STRING }, 
	FEDERAL_EIN: { type: Sequelize.STRING }, 
	Active: { type: Sequelize.STRING }, 
	DateCreated: { type: Sequelize.STRING }, 
	LastModifiedDate: { type: Sequelize.STRING }, 
	DatabaseCompanyID: { type: Sequelize.STRING }, 
	ORG_ADR1: { type: Sequelize.STRING }, 
	ORG_ADR2: { type: Sequelize.STRING }, 
	ORG_CITY: { type: Sequelize.STRING }, 
	ORG_STATE: { type: Sequelize.STRING }, 
	ORG_ZIP: { type: Sequelize.STRING }, 
	ORG_ZIP4: { type: Sequelize.STRING }, 
	EMPTY: { type: Sequelize.STRING },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'HIOS'	
});
hiosModel.removeAttribute('id');

const Hios = cciiodb.models.HIOS;

 const npiModel = npidb.define('npi', {
	NPI: { type: Sequelize.INTEGER},
	organization_name: { type: Sequelize.STRING },
	other_organization_name: { type: Sequelize.STRING },
	other_organization_name_type: { type: Sequelize.STRING },
	last_name: { type: Sequelize.STRING },
	first_name: { type: Sequelize.STRING },
	middle_name: { type: Sequelize.STRING },
	prefix: { type: Sequelize.STRING },
	suffix: { type: Sequelize.STRING },
	credential: { type: Sequelize.STRING },
	other_last_name: { type: Sequelize.STRING },
	other_first_name: { type: Sequelize.STRING },
	other_middle_name: { type: Sequelize.STRING },
	other_prefix: { type: Sequelize.STRING },
	other_suffix: { type: Sequelize.STRING },
	other_credential: { type: Sequelize.STRING },
	other_last_name_type: { type: Sequelize.STRING },
	mailing_address_first_line: { type: Sequelize.STRING },
	mailing_address_second_line: { type: Sequelize.STRING },
	mailing_address_city: { type: Sequelize.STRING },
	mailing_address_state: { type: Sequelize.STRING },
	mailing_address_postal_code: { type: Sequelize.STRING },
	mailing_address_country_code: { type: Sequelize.STRING },
	mailing_address_telephone: { type: Sequelize.STRING },
	mailing_address_fax: { type: Sequelize.STRING },
	practice_location_first_line: { type: Sequelize.STRING },
	practice_location_second_line: { type: Sequelize.STRING },
	practice_location_city: { type: Sequelize.STRING },
	practice_location_state: { type: Sequelize.STRING },
	practice_location_postal_code: { type: Sequelize.STRING },
	practice_location_country_code: { type: Sequelize.STRING },
	practice_location_telephone: { type: Sequelize.STRING },
	practice_location_fax: { type: Sequelize.STRING },
	authorized_official_last_name: { type: Sequelize.STRING },
	authorized_official_first_name: { type: Sequelize.STRING },
	authorized_official_middle_name: { type: Sequelize.STRING },
	authorized_official_title: { type: Sequelize.STRING },
	authorized_official_telephone: { type: Sequelize.STRING },
	authorized_official_name_prefix: { type: Sequelize.STRING },
	authorized_official_name_suffix: { type: Sequelize.STRING },
	authorized_official_credential: { type: Sequelize.STRING },
	deactivation_reason: { type: Sequelize.STRING },
	deactivation_date: { type: Sequelize.STRING },
	reactivation_date: { type: Sequelize.STRING },
	gender: { type: Sequelize.STRING },
	healthcare_taxonomy_code_1: { type: Sequelize.STRING },
	healthcare_taxonomy_code_2: { type: Sequelize.STRING },
	healthcare_taxonomy_code_3: { type: Sequelize.STRING },
	healthcare_taxonomy_code_4: { type: Sequelize.STRING },
	healthcare_taxonomy_code_5: { type: Sequelize.STRING },
	healthcare_taxonomy_code_6: { type: Sequelize.STRING },
	healthcare_taxonomy_code_7: { type: Sequelize.STRING },
	healthcare_taxonomy_code_8: { type: Sequelize.STRING },
	healthcare_taxonomy_code_9: { type: Sequelize.STRING },
	healthcare_taxonomy_code_10: { type: Sequelize.STRING },
	healthcare_taxonomy_code_11: { type: Sequelize.STRING },
	healthcare_taxonomy_code_12: { type: Sequelize.STRING },
	healthcare_taxonomy_code_13: { type: Sequelize.STRING },
	healthcare_taxonomy_code_14: { type: Sequelize.STRING },
	healthcare_taxonomy_code_15: { type: Sequelize.STRING },
	license_num_1: { type: Sequelize.STRING },
	license_num_state_1: { type: Sequelize.STRING },
	healthcare_primary_taxonomy_switch_1: { type: Sequelize.STRING },
	other_identifier_1: { type: Sequelize.STRING },
	other_identifier_type_code_1: { type: Sequelize.STRING },
	other_identifier_state_1: { type: Sequelize.STRING },
	other_identifier_issuer_1: { type: Sequelize.STRING },
	is_sole_proprietor: { type: Sequelize.STRING },
	is_subpart: { type: Sequelize.STRING },
	parent_name: { type: Sequelize.STRING },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'npi'	
});
npiModel.removeAttribute('id');

const Npi = npidb.models.npi;

const spdOrgModel = spddb.define('vhdir_organization', {
  organization_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
  meta_data_id: { type: Sequelize.INTEGER},
  active: { type: Sequelize.STRING},
  name: { type: Sequelize.STRING },
  description: { type: Sequelize.STRING},
  partOf_organization_id: { type: Sequelize.INTEGER},
  partOf_organization_name: { type: Sequelize.STRING},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_organization'	
});
spdOrgModel.removeAttribute('id');

const spdProviderModel = spddb.define('vhdir_practitioner', {
  practitioner_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
  meta_data_id: { type: Sequelize.INTEGER},
  active: { type: Sequelize.STRING},
  gender: { type: Sequelize.STRING },
  birthDate: { type: Sequelize.STRING},
  photo: { type: Sequelize.STRING},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_practitioner'	
});
spdProviderModel.removeAttribute('id');

const spdProviderRoleModel = spddb.define('vhdir_practitioner_role', {
  practitioner_role_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
  meta_data_id: { type: Sequelize.INTEGER},
  active: { type: Sequelize.STRING},
  period_start: { type: Sequelize.STRING },
  period_end: { type: Sequelize.STRING},
  practitioner_id: { type: Sequelize.INTEGER },
  organization_id: { type: Sequelize.INTEGER },
  availability_exceptions: { type: Sequelize.STRING},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_practitioner_role'	
});
spdProviderRoleModel.removeAttribute('id');

const spdAddressModel = spddb.define('address', {
	address_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true },
	use: { type: Sequelize.STRING },
	type: { type: Sequelize.STRING },
	text: { type: Sequelize.STRING },
	line1: { type: Sequelize.STRING },
	line2: { type: Sequelize.STRING },
	city: { type: Sequelize.STRING },
	district: { type: Sequelize.STRING },
	state: { type: Sequelize.STRING },
	postalCode: { type: Sequelize.STRING },
	country: { type: Sequelize.STRING },
	latitude: { type: Sequelize.STRING },
	longitude: { type: Sequelize.STRING },
	period_start: { type: Sequelize.STRING },
	period_end: { type: Sequelize.STRING },
	practitioner_id: { type: Sequelize.INTEGER },
	organization_id: { type: Sequelize.INTEGER },
	network_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'address'	
});
spdAddressModel.removeAttribute('id');

const spdTelecomModel = spddb.define('telecom', {
	telecom_id: { type: Sequelize.INTEGER,
					primaryKey: true,
				autoIncrement: true  },
	system: { type: Sequelize.STRING },
	value: { type: Sequelize.STRING },
	use: { type: Sequelize.STRING },
	rank: { type: Sequelize.STRING },
	period_start: { type: Sequelize.STRING },
	period_end: { type: Sequelize.STRING },
	organization_affiliation_id: { type: Sequelize.INTEGER },
	practitioner_id: { type: Sequelize.INTEGER },
	practitioner_role_id: { type: Sequelize.INTEGER },
	organization_id: { type: Sequelize.INTEGER },
	organization_contact_id: { type: Sequelize.INTEGER },
	careteam_id: { type: Sequelize.INTEGER },
	healthcare_service_id: { type: Sequelize.INTEGER },
	location_id: { type: Sequelize.INTEGER },
	contact_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'telecom'	
});
spdTelecomModel.removeAttribute('id');

const spdContactModel = spddb.define('contact', {
	contact_id: { type: Sequelize.INTEGER,
						primaryKey: true,
				autoIncrement: true },
	purpose_cc_id: { type: Sequelize.INTEGER },
	name_id: { type: Sequelize.INTEGER },
	address_id: { type: Sequelize.INTEGER },
	insurance_plan_id: { type: Sequelize.INTEGER },
	network_id: { type: Sequelize.INTEGER },
	organization_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'contact'	
});
spdContactModel.removeAttribute('id');

const spdNameModel = spddb.define('name', {
	name_id: { type: Sequelize.INTEGER,
						primaryKey: true,
				autoIncrement: true },
	use: { type: Sequelize.STRING },
	text: { type: Sequelize.STRING },
	family: { type: Sequelize.STRING },
	given: { type: Sequelize.STRING },
	prefix: { type: Sequelize.STRING },
	suffix: { type: Sequelize.STRING },
	period_start: { type: Sequelize.STRING },
	period_end: { type: Sequelize.STRING },
	practitioner_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'name'	
});
spdNameModel.removeAttribute('id');

const spdNetworkModel = spddb.define('vhdir_network', {
  network_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
  meta_data_id: { type: Sequelize.INTEGER},
  active: { type: Sequelize.STRING},
  name: { type: Sequelize.STRING },
  alias: { type: Sequelize.STRING },
  period_start: { type: Sequelize.STRING },
  period_end: { type: Sequelize.STRING },
  part_of_resource_reference_id: { type: Sequelize.INTEGER},
  practitioner_role_id: { type: Sequelize.INTEGER}, 
  organization_affiliation_id: { type: Sequelize.INTEGER}, 
  coverage_id: { type: Sequelize.INTEGER}, 
  plan_id: { type: Sequelize.INTEGER},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_network'	
});
spdNetworkModel.removeAttribute('id');

const spdIdentifierModel = spddb.define('identifier', {
	identifier_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
	identifier_status: { type: Sequelize.STRING },
	identifier_status_value_code: { type: Sequelize.STRING },
	use: { type: Sequelize.STRING },
	system: { type: Sequelize.STRING },
	value: { type: Sequelize.STRING },
	type_cc_id: { type: Sequelize.INTEGER},  
	period_start: { type: Sequelize.STRING }, 
	period_end: { type: Sequelize.STRING }, 
	endpoint_identifier_id: { type: Sequelize.INTEGER}, 
	healthcare_service_id: { type: Sequelize.INTEGER}, 
	insurance_plan_id: { type: Sequelize.INTEGER}, 
	practitioner_role_id: { type: Sequelize.INTEGER}, 
	practitioner_id: { type: Sequelize.INTEGER}, 
	qualification_id: { type: Sequelize.INTEGER}, 
	organization_id: { type: Sequelize.INTEGER}, 
	organization_affiliation_id: { type: Sequelize.INTEGER}, 
	careteam_id: { type: Sequelize.INTEGER}, 
	location_id: { type: Sequelize.INTEGER}, 
	network_id: { type: Sequelize.INTEGER}, 
	plan_id: { type: Sequelize.INTEGER},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'identifier'	
});
spdIdentifierModel.removeAttribute('id');

const spdReferenceModel = spddb.define('resource_reference', {
	resource_reference_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
	identifier: { type: Sequelize.STRING },
	reference: { type: Sequelize.STRING },
	display: { type: Sequelize.STRING },
	type: { type: Sequelize.INTEGER},  
	validation_target_id: { type: Sequelize.INTEGER},
	insurance_plan_network_id: { type: Sequelize.INTEGER},	
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'resource_reference'	
});
spdReferenceModel.removeAttribute('id');

const spdInsurancePlanModel = spddb.define('vhdir_insurance_plan', {
	insurance_plan_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true}, 
	meta_data_id: { type: Sequelize.INTEGER}, 
	status: { type: Sequelize.STRING }, 
	name: { type: Sequelize.STRING }, 
	alias: { type: Sequelize.STRING }, 
	period_start: { type: Sequelize.DATE }, 
	period_end: { type: Sequelize.DATE }, 
	organization_id: { type: Sequelize.INTEGER}, 
	ownedBy_reference_id: { type: Sequelize.INTEGER}, 
	administeredBy_reference_id: { type: Sequelize.INTEGER},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_insurance_plan'	
});
spdInsurancePlanModel.removeAttribute('id');

const spdHealthcareSvcModel = spddb.define('vhdir_healthcare_service', {
	healthcare_service_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
	meta_data_id: { type: Sequelize.INTEGER },
	active: { type: Sequelize.STRING },
	comment: { type: Sequelize.STRING },
	extra_details: { type: Sequelize.STRING },
	photo: { type: Sequelize.STRING },
	appointment_required: { type: Sequelize.STRING },
	availability_exceptions: { type: Sequelize.STRING },
	provided_by_organization_id: { type: Sequelize.INTEGER },
	practitioner_role_id: { type: Sequelize.INTEGER },
	organization_affiliation_id: { type: Sequelize.INTEGER },
	careteam_id: { type: Sequelize.INTEGER },
	name: { type: Sequelize.STRING },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_healthcare_service'	
});
spdHealthcareSvcModel.removeAttribute('id');

const spdRestrictionModel = spddb.define('vhdir_restriction', {
	restriction_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true }, 
	meta_data_id: { type: Sequelize.INTEGER }, 
	status: { type: Sequelize.STRING }, 
	scope_cc_id: { type: Sequelize.INTEGER }, 
	date_time: { type: Sequelize.DATE }, 
	provision_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_restriction'	
});
spdRestrictionModel.removeAttribute('id');

const spdLocationModel = spddb.define('vhdir_location', {
	location_id: { type: Sequelize.INTEGER,
					primaryKey: true,
				autoIncrement: true },
	meta_data_id: { type: Sequelize.INTEGER },
	status: { type: Sequelize.STRING },
	name: { type: Sequelize.STRING },
	alias: { type: Sequelize.STRING },
	description: { type: Sequelize.STRING },
	location_boundary_geojson: { type: Sequelize.STRING },
	latitude: { type: Sequelize.STRING },
	longitude: { type: Sequelize.STRING },
	altitude: { type: Sequelize.STRING },
	availability_exceptions: { type: Sequelize.STRING },
	practitioner_role_id: { type: Sequelize.INTEGER },
	organization_affiliation_id: { type: Sequelize.INTEGER },
	careteam_id: { type: Sequelize.INTEGER },
	healthcare_service_id: { type: Sequelize.INTEGER },
	healthcare_service_coverage_area_id: { type: Sequelize.INTEGER },
	address_id: { type: Sequelize.INTEGER },
	managing_organization_id: { type: Sequelize.INTEGER },
	part_of_location_id: { type: Sequelize.INTEGER },
	network_reference_id: { type: Sequelize.INTEGER },
	insurance_plan_coverage_area_id: { type: Sequelize.INTEGER },
	plan_coverage_area_id: { type: Sequelize.INTEGER },
	qualification_where_valid_id: { type: Sequelize.INTEGER },
	physical_type_cc_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_location'	
});
spdLocationModel.removeAttribute('id');

const spdValidationModel = spddb.define('vhdir_validation', {
	validation_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true }, 
	meta_data_id: { type: Sequelize.INTEGER },
	need_cc_id: { type: Sequelize.INTEGER },
	target_location: { type: Sequelize.STRING },
	status: { type: Sequelize.STRING },
	status_date: { type: Sequelize.DATE },
	validation_type_cc_id: { type: Sequelize.INTEGER },
	frequency: { type: Sequelize.STRING },
	last_performed: { type: Sequelize.DATE },
	next_scheduled: { type: Sequelize.DATE },
	failure_action_cc_id: { type: Sequelize.INTEGER },
	attestation_id: { type: Sequelize.INTEGER },
	validator_organization_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'vhdir_validation'	
});
spdValidationModel.removeAttribute('id');

const spdOrgAliasModel = spddb.define('organization_alias', {
  organization_alias_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
  period_start: { type: Sequelize.DATE},
  period_end: { type: Sequelize.DATE},
  value: { type: Sequelize.STRING },
  alias_type_cc_id: { type: Sequelize.INTEGER},
  organization_id: { type: Sequelize.INTEGER},
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'organization_alias'	
});
spdOrgAliasModel.removeAttribute('id');

const spdFhirCodeableConceptModel = spddb.define('fhir_codeable_concept', {
	codeable_concept_id: { type: Sequelize.INTEGER,
				primaryKey: true,
				autoIncrement: true},
	text: { type: Sequelize.STRING },
	coding_system: { type: Sequelize.STRING },
	coding_version: { type: Sequelize.STRING },
	coding_code: { type: Sequelize.STRING },
	coding_display: { type: Sequelize.STRING },
	coding_user_selected: { type: Sequelize.STRING },
	practitioner_role_specialty_id: { type: Sequelize.INTEGER },
	practitioner_role_code_id: { type: Sequelize.INTEGER },
	organization_affiliation_code_id: { type: Sequelize.INTEGER },
	organization_affiliation_specialty_id: { type: Sequelize.INTEGER },
	careteam_category_id: { type: Sequelize.INTEGER },
	endpoint_payload_type_id: { type: Sequelize.STRING },
	organization_type_id: { type: Sequelize.INTEGER },
	practitioner_accessibility_id: { type: Sequelize.INTEGER },
	qualification_communication_id: { type: Sequelize.INTEGER },
	healthcare_service_category_id: { type: Sequelize.INTEGER },
	healthcare_service_type_id: { type: Sequelize.INTEGER },
	healthcare_service_specialty_id: { type: Sequelize.INTEGER },
	healthcare_service_service_provision_code_id: { type: Sequelize.INTEGER },
	healthcare_service_program_id: { type: Sequelize.INTEGER },
	healthcare_service_characteristic_id: { type: Sequelize.INTEGER },
	healthcare_service_communication_id: { type: Sequelize.INTEGER },
	healthcare_service_referral_method_id: { type: Sequelize.INTEGER },
	location_accessibility_id: { type: Sequelize.INTEGER },
	location_type_id: { type: Sequelize.INTEGER },
	network_type_id: { type: Sequelize.INTEGER },
	insurance_plan_type_id: { type: Sequelize.INTEGER },
	validation_process_id: { type: Sequelize.INTEGER },
	careteam_participant_role_id: { type: Sequelize.INTEGER },
	primary_source_type_id: { type: Sequelize.INTEGER },
	primary_source_communication_method_id: { type: Sequelize.INTEGER },
	primary_source_push_type_available_id: { type: Sequelize.INTEGER },
	plan_cost_qualifier: { type: Sequelize.STRING },
	qualification_where_valid_id: { type: Sequelize.INTEGER },
	restriction_category_id: { type: Sequelize.INTEGER },
	ehr_patient_acces_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'fhir_codeable_concept'	
});
spdFhirCodeableConceptModel.removeAttribute('id');

spdOrgModel.hasMany(spdAddressModel, {foreignKey: 'organization_id', sourceKey: 'organization_id'});
spdAddressModel.belongsTo(spdOrgModel, {foreignKey: 'organization_id', targetKey: 'organization_id'});
spdOrgModel.hasMany(spdTelecomModel, {foreignKey: 'organization_id', sourceKey: 'organization_id'});
spdTelecomModel.belongsTo(spdOrgModel, {foreignKey: 'organization_id', targetKey: 'organization_id'});
spdOrgModel.hasMany(spdContactModel, {foreignKey: 'organization_id', sourceKey: 'organization_id'});
spdContactModel.belongsTo(spdOrgModel, {foreignKey: 'organization_id', targetKey: 'organization_id'});
spdContactModel.hasOne(spdNameModel, {foreignKey: 'name_id', sourceKey: 'name_id'});



const Organization = spddb.models.vhdir_organization;
const Address = spddb.models.address;
const Telecom = spddb.models.telecom;
const Contact = spddb.models.contact;
const Name = spddb.models.name;
const Provider = spddb.models.vhdir_practitioner;
const Network = spddb.models.vhdir_network;
const Identifier = spddb.models.identifier;
const Reference = spddb.models.resource_reference;
const SpdInsurancePlan = spddb.models.vhdir_insurance_plan;
const spdHealthcareSvc = spddb.models.vhdir_healthcare_service
const SpdRestriction = spddb.models.vhdir_restriction;
const SpdValidation = spddb.models.vhdir_validation;
const SpdLocation = spddb.models.vhdir_location;
const OrgAlias = spddb.models.organization_alias;
const ProviderRole = spddb.models.vhdir_practitioner_role;
const FhirCodeableConcept = spddb.models.fhir_codeable_concept


export { Npi, npidb, Organization, Address, Telecom, Contact, Name, Provider, spddb, cciiodb, Cciio, Hios, Network, Identifier, Reference, InsurancePlan, SpdInsurancePlan, OrgAlias, ProviderRole, FhirCodeableConcept, spdHealthcareSvc, SpdRestriction, SpdValidation, SpdLocation};