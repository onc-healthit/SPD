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
				primaryKey: true},
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
				primaryKey: true},
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

const spdAddressModel = spddb.define('address', {
	address_id: { type: Sequelize.INTEGER,
				primaryKey: true },
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
					primaryKey: true  },
	system: { type: Sequelize.STRING },
	value: { type: Sequelize.STRING },
	use: { type: Sequelize.STRING },
	rank: { type: Sequelize.STRING },
	period_start: { type: Sequelize.STRING },
	period_end: { type: Sequelize.STRING },
	organization_affiliation_id: { type: Sequelize.INTEGER },
	practitioner_id: { type: Sequelize.INTEGER },
	practitioner_role_id: { type: Sequelize.INTEGER },
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

const spdOrgContactModel = spddb.define('organization_contact', {
	organization_contact_id: { type: Sequelize.INTEGER,
						primaryKey: true },
	purpose_id: { type: Sequelize.INTEGER },
	name_id: { type: Sequelize.INTEGER },
	address_id: { type: Sequelize.INTEGER },
	organization_id: { type: Sequelize.INTEGER },
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'organization_contact'	
});
spdOrgContactModel.removeAttribute('id');

const spdNameModel = spddb.define('name', {
	name_id: { type: Sequelize.INTEGER,
						primaryKey: true },
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
				primaryKey: true},
  meta_data_id: { type: Sequelize.INTEGER},
  active: { type: Sequelize.STRING},
  name: { type: Sequelize.STRING },
  alias: { type: Sequelize.STRING },
  period_start: { type: Sequelize.STRING },
  period_end: { type: Sequelize.STRING },
  part_of_resource_reference_id: { type: Sequelize.INTEGER},
  practitioner_role_id: { type: Sequelize.INTEGER}, 
  organization_affiliation_id: { type: Sequelize.INTEGER}, 
  insurance_plan_id: { type: Sequelize.INTEGER}, 
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
				primaryKey: true},
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
				primaryKey: true},
	identifier: { type: Sequelize.STRING },
	reference: { type: Sequelize.STRING },
	display: { type: Sequelize.STRING },
	type: { type: Sequelize.INTEGER},  
	validation_target_id: { type: Sequelize.INTEGER}, 
}, {
	timestamps: false,
	underscored: true,
	freezeTableName: true,
	tableName: 'resource_reference'	
});
spdReferenceModel.removeAttribute('id');

spdOrgModel.hasMany(spdAddressModel, {foreignKey: 'organization_id', sourceKey: 'organization_id'});
spdAddressModel.belongsTo(spdOrgModel, {foreignKey: 'organization_id', targetKey: 'organization_id'});
spdOrgModel.hasMany(spdTelecomModel, {foreignKey: 'organization_id', sourceKey: 'organization_id'});
spdTelecomModel.belongsTo(spdOrgModel, {foreignKey: 'organization_id', targetKey: 'organization_id'});
spdOrgModel.hasMany(spdOrgContactModel, {foreignKey: 'organization_id', sourceKey: 'organization_id'});
spdOrgContactModel.belongsTo(spdOrgModel, {foreignKey: 'organization_id', targetKey: 'organization_id'});
spdOrgContactModel.hasOne(spdNameModel, {foreignKey: 'name_id', sourceKey: 'name_id'});



const Organization = spddb.models.vhdir_organization;
const Address = spddb.models.address;
const Telecom = spddb.models.telecom;
const Contact = spddb.models.organization_contact;
const Name = spddb.models.name;
const Provider = spddb.models.vhdir_practitioner;
const Network = spddb.models.vhdir_network;
const Identifier = spddb.models.identifier;
const Reference = spddb.models.resource_reference



export { Npi, npidb, Organization, Address, Telecom, Contact, Name, Provider, spddb, cciiodb, Cciio, Hios, Network, Identifier, Reference };