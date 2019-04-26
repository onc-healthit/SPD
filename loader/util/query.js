import { Npi, npidb, Organization, Address, Telecom, Contact, Name, Provider, cciiodb, Cciio, Hios, Network, Identifier, Reference, InsurancePlan, SpdInsurancePlan, OrgAlias} from '../model/sequelizeModels';
import Sequelize from 'sequelize';

export const query = {
	/*async getNoOrg(){
		var plans = await InsurancePlan.findAll({
			order: [['PlanMarketingName']],
			offset: 12000, 
			limit: 4000});
		var currentName = '';
		for (var i = 0; i < plans.length; i++) {
			console.log("Plan Name: "+plans[i].PlanMarketingName);
			if (plans[i].PlanMarketingName === currentName) {
				continue;
			}
			currentName = plans[i].PlanMarketingName;			
			var hiosOrg = await Hios.findOne({where: {HIOS_ISSUER_ID: plans[i].IssuerId}}); 
			if (hiosOrg === null) {
				console.log("Org not found: "+plans[i].IssuerId);				
			}
		}
		
	},*/
	async getInsurancePlans() {
		var plans = await InsurancePlan.findAll({
			order: [['PlanMarketingName']],
			offset: 10000, 
			limit: 5000});
		var currentName = '';
		for (var i = 0; i < plans.length; i++) {
			console.log("Plan Name: "+plans[i].PlanMarketingName);
			var ownerId = 0;
			if (plans[i].PlanMarketingName === currentName) {
			console.log("Plan exist: "+plans[i].PlanMarketingName);
				var exist = await SpdInsurancePlan.findOne({
					where: {
						name: plans[i].PlanMarketingName
					}
				});
				if (exist !== null) {
					var id = await Identifier.create({
						identifier_status_value_code: "active",
						use: "official",
						value: plans[i].PlanId,
						insurance_plan_id: exist.insurance_plan_id
					});																
				}
				continue;
			}
			currentName = plans[i].PlanMarketingName;
			var id = await Identifier.findOne({
				where: {
					use: 'secondary',
					value: plans[i].IssuerId
				}
			});
			if (id == null) {
				var hiosOrg = await Hios.findOne({where: {HIOS_ISSUER_ID: plans[i].IssuerId}}); 
				if (hiosOrg != null) {
					var tempName = 'EMPTY';
					if (hiosOrg.ISSR_LGL_NAME != null && hiosOrg.ISSR_LGL_NAME.length > 0){
						tempName = hiosOrg.ISSR_LGL_NAME;
					}
					var created = await Organization.create({
					  active: '1',
					  name: tempName});
					var orgCreated = await Organization.findOne({where: {name: tempName}});
					if (hiosOrg.ORG_STATE.length <= 2) {
					console.log("Good State: "+hiosOrg.ORG_STATE);
						var address1 = await Address.create({
							use: "work",
							line1: hiosOrg.ORG_ADR1,
							line2: hiosOrg.ORG_ADR2,
							city: hiosOrg.ORG_CITY, 
							state: hiosOrg.ORG_STATE,
							postalCode: hiosOrg.ORG_ZIP,
							country: 'USA',
							organization_id: orgCreated.organization_id
						});							
					}
					else {
					console.log("Bad State: "+hiosOrg.ORG_STATE);
						var address1 = await Address.create({
							use: "work",
							line1: hiosOrg.ORG_ADR2,
							line2: hiosOrg.ORG_CITY, 
							city: hiosOrg.ORG_STATE,
							state: hiosOrg.ORG_ZIP,
							postalCode: hiosOrg.ORG_ZIP4,
							country: 'USA',
							organization_id: orgCreated.organization_id
						});														
					}
					var id = await Identifier.create({
						identifier_status_value_code: "active",
						use: "secondary",
						value: hiosOrg.HIOS_ISSUER_ID,
						organization_id: orgCreated.organization_id
					});
					ownerId = orgCreated.organization_id;					
				}
				else {
					console.log("Owner not found: "+plans[i].PlanMarketingName+" SKIP!!!");
					continue;
				}
			}
			else {
				ownerId = id.organization_id;
			}
			var plan = await SpdInsurancePlan.create({
				status: 'active',
				name: plans[i].PlanMarketingName,
				period_start: plans[i].PlanEffectiveDate,
				period_end: plans[i].PlanExpirationDate,
				owned_by_organization_id: ownerId,
				administered_by_organization_id: ownerId
			});
			var planCreated = await SpdInsurancePlan.findOne({
				where: {
					name: plans[i].PlanMarketingName
				}
			});
			var id = await Identifier.create({
				identifier_status_value_code: "active",
				use: "official",
				value: plans[i].PlanId,
				insurance_plan_id: planCreated.insurance_plan_id
			});											
			
		}
	},
	async getNetworks() {
		var cciioQuery = "SELECT NetworkId, SourceName, IssuerId,"+
			" NetworkName FROM cciio.network"+
			" WHERE SourceName='HIOS' ";
		var res = await cciiodb.query(cciioQuery, { model: Cciio } );
		var nwId = '', names = [], owner = null, org = null, orgId = null, orgName = null ;
		for (var i = 0; i < res.length; i++) {
			if (res[i].NetworkId != nwId){
				owner = await Hios.findOne({where: {HIOS_ISSUER_ID: res[i].IssuerId}}); 
				if (owner != null) {
					org = await Organization.findOne({where: {name: owner.ISSR_LGL_NAME}});
					if (org != null) {
						console.log("Org Found: "+JSON.stringify(org));
						//orgId = org.organization_id
						orgName = org.name;
					}
					else {
						var tempName = 'EMPTY';
						if (owner.ISSR_LGL_NAME != null && owner.ISSR_LGL_NAME.length > 0){
							tempName = owner.ISSR_LGL_NAME;
						}
						var created = await Organization.create({
						  active: '1',
						  name: tempName});
						//console.log("Org: "+JSON.stringify(created));
						var orgCreated = await Organization.findOne({where: {name: tempName}});
						//console.log("OrgCreated: "+JSON.stringify(orgCreated));
						if (owner.ORG_STATE.length <= 2) {
						console.log("Good State: "+owner.ORG_STATE);
							var address1 = await Address.create({
								use: "work",
								line1: owner.ORG_ADR1,
								line2: owner.ORG_ADR2,
								city: owner.ORG_CITY, 
								state: owner.ORG_STATE,
								postalCode: owner.ORG_ZIP,
								country: 'USA',
								organization_id: orgCreated.organization_id
							});							
						}
						else {
						console.log("Bad State: "+owner.ORG_STATE);
							var address1 = await Address.create({
								use: "work",
								line1: owner.ORG_ADR2,
								line2: owner.ORG_CITY, 
								city: owner.ORG_STATE,
								state: owner.ORG_ZIP,
								postalCode: owner.ORG_ZIP4,
								country: 'USA',
								organization_id: orgCreated.organization_id
							});														
						}
						var id = await Identifier.create({
							identifier_status_value_code: "active",
							use: "secondary",
							value: owner.HIOS_ISSUER_ID,
							organization_id: orgCreated.organization_id
						});											
						//orgId = orgCreated.organization_id
						orgName = orgCreated.name;
					}
				}
				if (names.length > 0) {
					var refId = null;
					var ref = await Reference.findOne({where:
						{
							type: 'vhdir_organization',
							identifier: orgName
						}
						});
					if (ref == null) {
						var tempRef = await Reference.create({
							type: 'vhdir_organization',
							identifier: orgName
						});
						var refCreated = await Reference.findOne({where:
						{
							type: 'vhdir_organization',
							identifier: orgName
						}
						});
						refId = refCreated.resource_reference_id
					}
					else {
						refId = ref.resource_reference_id;						
					}
					var nw = await Network.create({
						active: '1',
						alias: names.join(";"),
						part_of_resource_reference_id: refId
					});
					var nwCreated = await Network.findOne({where: {alias: names.join(";")}});
					var id = await Identifier.create({
						identifier_status_value_code: "active",
						use: "official",
						value: nwId,
						network_id: nwCreated.network_id
					});					
				}
				nwId = res[i].NetworkId;
				names = [];
				names.push(res[i].NetworkName);
			}
			else {
				names.push(res[i].NetworkName);
			}
		}
	},
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
		"`NPI Deactivation Reason Code` as deactivation_reason, "+
		"`NPI Deactivation Date` as deactivation_date, "+
		"`NPI Reactivation Date` as reactivation_date, "+
		"`Is Organization Subpart` as is_subpart,"+
		"`Parent Organization LBN` as parent_name FROM nppes.npi WHERE `Entity Type Code` = 2 "+
		"order by `Provider Organization Name (Legal Business Name)` , "+
		"`Provider Other Organization Name`, `Other Provider Identifier_1`, "+
		"`Provider First Line Business Mailing Address`, "+
        "`Provider First Line Business Practice Location Address` limit 8000, 2000";
		var res = await npidb.query(npiQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		var lbn = "", olbn = null, oid = null, fmailing = "", floc = "", nameToUse = null;
		for (var i = 0; i < res.length; i++) {
			//var org = await Organization.findOne({where: {name: res[i].organization_name}});
			//console.log("org: "+JSON.stringify(org));
			if (res[i].organization_name != lbn) {
				nameToUse = res[i].organization_name;
				lbn = res[i].organization_name;
			}
			else if (res[i].other_organization_name != null && 
				res[i].other_organization_name.length > 0 && 
				res[i].other_organization_name != olbn ){
				nameToUse = res[i].other_organization_name;
				olbn = res[i].other_organization_name;					
			}
			else if (res[i].other_identifier_1 != null && 
				res[i].other_identifier_1.length > 0 && 
				res[i].other_identifier_1 != oid ){
				nameToUse = res[i].organization_name + res[i].other_identifier_1;
				oid = res[i].other_identifier_1;					
			}
			else if (res[i].mailing_address_first_line != null && 
				res[i].mailing_address_first_line.length > 0 && 
				res[i].mailing_address_first_line != fmailing ){
				nameToUse = res[i].organization_name + res[i].mailing_address_first_line;
				fmailing = res[i].mailing_address_first_line;					
			}
			else if (res[i].practice_location_first_line != null && 
				res[i].practice_location_first_line.length > 0 && 
				res[i].practice_location_first_line != floc ){
				nameToUse = res[i].organization_name + res[i].practice_location_first_line;
				floc = res[i].practice_location_first_line;					
			}
			//temp
			var exist = await Organization.findOne({where: {name: nameToUse}});
			if (exist != null) {
				console.log("Org Found" + exist.organization_id);
				/*if (res[i].other_organization_name!= null && res[i].other_organization_name.length > 0){
					var alias1 = await OrgAlias.create({
						value: res[i].other_organization_name,
						organization_id: exist.organization_id
					});
				}
				if (res[i].other_identifier_1!= null && res[i].other_identifier_1.length > 0){
					var alias1 = await OrgAlias.create({
						value: res[i].other_identifier_1,
						organization_id: exist.organization_id
					});
				}
				if (res[i].deactivation_date != null && res[i].deactivation_date.length > 4 
					&& (res[i].reactivation_date === null || res[i].reactivation_date.length <= 0)) {
					Organization.update({
						active: '0'
					},
					{where: {name: nameToUse}
					});	
					console.log("Status updated" + exist.organization_id);
				}
				var contactExisting = await Contact.findOne({where: {
				organization_id: exist.organization_id				
				}});
				if (contactExisting != null){
					var telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].authorized_official_telephone,
					organization_contact_id: contactExisting.organization_contact_id,
					organization_id: exist.organization_id
					});
				}
				var telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].mailing_address_telephone,
					organization_id: exist.organization_id
					});
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].mailing_address_fax,
					organization_id: exist.organization_id									
					});
				telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].practice_location_telephone,
					organization_id: exist.organization_id									
					});
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].practice_location_fax,
					organization_id: exist.organization_id									
					});
				var id = await Identifier.create({
					identifier_status_value_code: "active",
					use: "npi",
					value: res[i].NPI,
					organization_id: exist.organization_id
				});	*/
				continue;
			}
			console.log("Org Not Found: to create " + nameToUse)
			var isActive = '1';
			if (res[i].deactivation_date != null && res[i].deactivation_date.length > 4 
				&& (res[i].reactivation_date === null || res[i].reactivation_date.length <= 0)) {
				isActive = '0';
				console.log("Org inactive: " + nameToUse);
			}
			var created = await Organization.create({
			  active: isActive,
			  name: nameToUse,
			partOf_organization_name: res[i].parent_name});
			//console.log("Org: "+JSON.stringify(created));
			var orgCreated = await Organization.findOne({where: {name: nameToUse}});
			//console.log("OrgCreated: "+JSON.stringify(orgCreated));
			var idNpi = await Identifier.create({
				identifier_status_value_code: "active",
				use: "official",
				system: "http://hl7.org/fhir/sid/us-npi",
				value: res[i].NPI,
				organization_id: orgCreated.organization_id
			});	
			if (res[i].other_organization_name!= null && res[i].other_organization_name.length > 0){
				var alias1 = await OrgAlias.create({
					value: res[i].other_organization_name,
					organization_id: orgCreated.organization_id
				});
			}
			if (res[i].other_identifier_1!= null && res[i].other_identifier_1.length > 0){
				var alias1 = await OrgAlias.create({
					value: res[i].other_identifier_1,
					organization_id: orgCreated.organization_id
				});
			}
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
			var contact = await Contact.create({
				name_id: nameCreated.name_id,
				organization_id: orgCreated.organization_id
			})
			var contactCreated = await Contact.findOne({where: {
				name_id: nameCreated.name_id,
				organization_id: orgCreated.organization_id				
			}});
			var telecoms = await Telecom.create(
			{
			system: "phone",
			value: res[i].authorized_official_telephone,
			organization_contact_id: contactCreated.organization_contact_id,
			organization_id: orgCreated.organization_id
			});
			
			//console.log("Contact: "+JSON.stringify(contact));
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
		"limit 0, 10000";
		var res = await npidb.query(providerQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		for (var i = 0; i < res.length; i++) {
			var fullName = res[i].last_name+res[i].first_name+res[i].middle_name;
			//console.log("full name: "+JSON.stringify(fullName));
			var provider = await Provider.findOne({where: {photo: fullName}});
			//console.log("provider: "+JSON.stringify(provider));
			if (provider != null){
				console.log("provider found "+provider.photo);
				var idNpi = await Identifier.create({
					identifier_status_value_code: "active",
					use: "official",
					system: "http://hl7.org/fhir/sid/us-npi",
					value: res[i].NPI,
					practitioner_id: provider.practitioner_id
				});	
				continue;
			}
			if (provider == null) {
				var isActive = '1';
				if (res[i].deactivation_date != null && res[i].deactivation_date.length > 4 
					&& (res[i].reactivation_date === null || res[i].reactivation_date.length <= 0)) {
					isActive = '0';
					console.log("Provider inactive: " + fullName);
				}
				var created = await Provider.create({
					active: isActive,
					gender: res[i].gender,
					photo: res[i].last_name+res[i].first_name+res[i].middle_name});
				//console.log("Provider: "+JSON.stringify(created));
				var providerCreated = await Provider.findOne({where: {photo: res[i].last_name+res[i].first_name+res[i].middle_name}});
				//console.log("ProviderCreated: "+JSON.stringify(providerCreated));
				var idNpi = await Identifier.create({
					identifier_status_value_code: "active",
					use: "official",
					system: "http://hl7.org/fhir/sid/us-npi",
					value: res[i].NPI,
					practitioner_id: providerCreated.practitioner_id
				});	
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
				//console.log("address: "+JSON.stringify(address2));
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
				//console.log("telecoms: "+JSON.stringify(telecoms));
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