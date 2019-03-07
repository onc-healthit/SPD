import { Npi, npidb, Organization, Address, Telecom, Contact, Name, Provider } from '../model/sequelizeModels';
import Sequelize from 'sequelize';

export const query = {
	async getOrgs() {
		var npiQuery = "SELECT NPI, `Provider Organization Name (Legal Business Name)` as organization_name,"+
		"`Provider Other Organization Name` as other_organization_name,"+
		"`Provider Other Organization Name Type Code` as other_organization_name_type,"+
		"`Provider First Line Business Mailing Address`  as mailing_address_first_line,"+
		"`Provider Second Line Business Mailing Address` as mailing_address_second_line,"+
		"`Provider Business Mailing Address City Name` as mailing_address_city,"+
		"`Provider Business Mailing Address State Name` as mailing_address_state,"+
		"`Provider Business Mailing Address Postal Code` as mailing_address_postal_code,"+
		"`Provider Business Mailing Address Country Code` as mailing_address_country_code,"+
		"`Provider Business Mailing Address Telephone Number` as mailing_address_telephone,"+
		"`Provider Business Mailing Address Fax Number` as mailing_address_fax,"+
		"`Provider First Line Business Practice Location Address` as practice_location_first_line,"+
		"`Provider Second Line Business Practice Location Address` as practice_location_second_line,"+
		"`Provider Business Practice Location Address City Name` as practice_location_city,"+
		"`Provider Business Practice Location Address State Name` as practice_location_state,"+
		"`Provider Business Practice Location Address Postal Code` as practice_location_postal_code,"+
		"`Provider Business Practice Location Address Ctry Code` as practice_location_country_code,"+
		"`Provider Business Practice Location Address Telephone Number` as practice_location_telephone,"+
		"`Provider Business Practice Location Address Fax Number` as practice_location_fax,"+
		"`Authorized Official Last Name` as authorized_official_last_name,"+
		"`Authorized Official First Name` as authorized_official_first_name,"+
		"`Authorized Official Middle Name` as authorized_official_middle_name,"+
		"`Authorized Official Title or Position` as authorized_official_title,"+
		"`Authorized Official Telephone Number` as authorized_official_telephone,"+
		"`Authorized Official Name Prefix Text` as authorized_official_name_prefix,"+
		"`Authorized Official Name Suffix Text` as authorized_official_name_suffix,"+
		"`Authorized Official Credential Text` as authorized_official_credential,"+
		"`Other Provider Identifier_1` as other_identifier_1,"+
		"`Other Provider Identifier Type Code_1` as other_identifier_type_code_1,"+
		"`Other Provider Identifier State_1` as other_identifier_state_1,"+
		"`Other Provider Identifier Issuer_1` as other_identifier_issuer_1,"+
		"`Is Organization Subpart` as is_subpart,"+
		"`Parent Organization LBN` as parent_name FROM nppes.npi WHERE `Entity Type Code` = 2 "+
		"order by `Provider Organization Name (Legal Business Name)` limit 0, 100";
		var res = await npidb.query(npiQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		for (var i = 0; i < res.length; i++) {
			var org = await Organization.findOne({where: {name: res[i].organization_name}});
			//console.log("org: "+JSON.stringify(org));
			if (org == null) {
				var created = await Organization.create({
				  active: '1',
				  name: res[i].organization_name,
				partOf_organization_name: res[i].parent_name});
				//console.log("Org: "+JSON.stringify(created));
				var orgCreated = await Organization.findOne({where: {name: res[i].organization_name}});
				//console.log("OrgCreated: "+JSON.stringify(orgCreated));
				var address1 = await Address.create({
					use: "billing",
					line1: res[i].mailing_address_first_line, 
					city: res[i].mailing_address_city, 
					state: res[i].mailing_address_state, 
					postalCode: res[i].mailing_address_postal_code,
					country: res[i].mailing_address_country_code,
					organization_id: orgCreated.organization_id
				});
				var address2 = await Address.create({					
					use: "work",
					line1: res[i].practice_location_first_line, 
					city: res[i].practice_location_city, 
					state: res[i].practice_location_state, 
					postalCode: res[i].practice_location_postal_code,
					country: res[i].practice_location_country_code,
					organization_id: orgCreated.organization_id
				});
				//console.log("address: "+JSON.stringify(address));
				var telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].mailing_address_telephone,
					organization_id: orgCreated.organization_id
					});
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].mailing_address_fax,
					organization_id: orgCreated.organization_id									
					});
				telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].practice_location_telephone,
					organization_id: orgCreated.organization_id									
					});
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].practice_location_fax,
					organization_id: orgCreated.organization_id									
					});
				//console.log("telecoms: "+JSON.stringify(telecoms));
				var name = await Name.create({
					use: "official",
					family: res[i].authorized_official_last_name,
					given: res[i].authorized_official_first_name,
				})
				//console.log("name: "+JSON.stringify(name));
				var nameCreated = await Name.findOne({where: {
					family: res[i].authorized_official_last_name,
					given: res[i].authorized_official_first_name}});
				//console.log("nameCreated: "+JSON.stringify(nameCreated));
				var contact = Contact.create({
					name_id: nameCreated.name_id,
					organization_id: orgCreated.organization_id
				})
				//console.log("Contact: "+JSON.stringify(contact));
			}
		}

	},
	async getProviders() {
		var providerQuery = "SELECT NPI, `Provider Last Name (Legal Name)` as last_name, "+
		"`Provider First Name` as first_name, "+
		"`Provider Middle Name` as middle_name, "+
		"`Provider Name Prefix Text` as prefix, "+
		"`Provider Name Suffix Text` as suffix, "+
		"`Provider Credential Text` as credential, "+
		"`Provider Other Last Name` as other_last_name, "+
		"`Provider Other First Name` as other_first_name, "+
		"`Provider Other Middle Name` as other_middle_name, "+
		"`Provider Other Name Prefix Text` as other_prefix, "+
		"`Provider Other Name Suffix Text` as other_suffix, "+
		"`Provider Other Credential Text` as other_credential, "+
		"`Provider Other Last Name Type Code` as other_last_name_type, "+
		"`Provider First Line Business Mailing Address`  as mailing_address_first_line,"+
		"`Provider Second Line Business Mailing Address` as mailing_address_second_line,"+
		"`Provider Business Mailing Address City Name` as mailing_address_city,"+
		"`Provider Business Mailing Address State Name` as mailing_address_state,"+
		"`Provider Business Mailing Address Postal Code` as mailing_address_postal_code,"+
		"`Provider Business Mailing Address Country Code` as mailing_address_country_code,"+
		"`Provider Business Mailing Address Telephone Number` as mailing_address_telephone,"+
		"`Provider Business Mailing Address Fax Number` as mailing_address_fax,"+
		"`Provider First Line Business Practice Location Address` as practice_location_first_line,"+
		"`Provider Second Line Business Practice Location Address` as practice_location_second_line,"+
		"`Provider Business Practice Location Address City Name` as practice_location_city,"+
		"`Provider Business Practice Location Address State Name` as practice_location_state,"+
		"`Provider Business Practice Location Address Postal Code` as practice_location_postal_code,"+
		"`Provider Business Practice Location Address Ctry Code` as practice_location_country_code,"+
		"`Provider Business Practice Location Address Telephone Number` as practice_location_telephone,"+
		"`Provider Business Practice Location Address Fax Number` as practice_location_fax,"+
		"`NPI Deactivation Reason Code` as deactivation_reason, "+
		"`NPI Deactivation Date` as deactivation_date, "+
		"`NPI Reactivation Date` as reactivation_date, "+
		"`Provider Gender Code` as gender, "+
		"`Healthcare Provider Taxonomy Code_1` as healthcare_taxonomy_code_1, "+
		"`Provider License Number_1` as license_num_1, "+
		"`Provider License Number State Code_1` as license_num_state_1, "+
		"`Healthcare Provider Primary Taxonomy Switch_1` as healthcare_primary_taxonomy_switch_1, "+
		"`Other Provider Identifier_1` as other_identifier_1,"+
		"`Other Provider Identifier Type Code_1` as other_identifier_type_code_1,"+
		"`Other Provider Identifier State_1` as other_identifier_state_1,"+
		"`Other Provider Identifier Issuer_1` as other_identifier_issuer_1,"+
		"`Is Sole Proprietor` as is_sole_proprietor "+
		"FROM nppes.npi WHERE `Entity Type Code` = 1 "+
		"order by `Provider Last Name (Legal Name)`, `Provider First Name`, `Provider Middle Name` "+
		"limit 0, 100";
		var res = await npidb.query(providerQuery, { model: Npi } );
		console.log("NPI result: "+JSON.stringify(res));
		for (var i = 0; i < res.length; i++) {
			var fullName = res[i].last_name+res[i].first_name+res[i].middle_name;
			//console.log("full name: "+JSON.stringify(fullName));
			var provider = await Provider.findOne({where: {photo: fullName}});
			console.log("provider: "+JSON.stringify(provider));
			if (provider == null) {
				var created = await Provider.create({
					active: '1',
					gender: res[i].gender,
					photo: res[i].last_name+res[i].first_name+res[i].middle_name});
				console.log("Provider: "+JSON.stringify(created));
				var providerCreated = await Provider.findOne({where: {photo: res[i].last_name+res[i].first_name+res[i].middle_name}});
				console.log("ProviderCreated: "+JSON.stringify(providerCreated));
				var address1 = await Address.create({
					use: "billing",
					line1: res[i].mailing_address_first_line, 
					city: res[i].mailing_address_city, 
					state: res[i].mailing_address_state, 
					postalCode: res[i].mailing_address_postal_code,
					country: res[i].mailing_address_country_code,
					practitioner_id: providerCreated.practitioner_id
				});
				var address2 = await Address.create({					
					use: "work",
					line1: res[i].practice_location_first_line, 
					city: res[i].practice_location_city, 
					state: res[i].practice_location_state, 
					postalCode: res[i].practice_location_postal_code,
					country: res[i].practice_location_country_code,
					practitioner_id: providerCreated.practitioner_id
				});
				console.log("address: "+JSON.stringify(address2));
				var telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].mailing_address_telephone,
					practitioner_id: providerCreated.practitioner_id
					});
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].mailing_address_fax,
					practitioner_id: providerCreated.practitioner_id									
					});
				telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].practice_location_telephone,
					practitioner_id: providerCreated.practitioner_id									
					});
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].practice_location_fax,
					practitioner_id: providerCreated.practitioner_id									
					});
				console.log("telecoms: "+JSON.stringify(telecoms));
				var name1 = await Name.create({
					use: "official",
					family: res[i].last_name,
					given: res[i].first_name,
					practitioner_id: providerCreated.practitioner_id									
				});
				var name2 = await Name.create({
					use: "usual",
					family: res[i].other_last_name,
					given: res[i].other_first_name,
					practitioner_id: providerCreated.practitioner_id									
				});
			}
		}

	}
};