import { Npi, npidb, spddb, Organization, Address, Telecom, Contact, Name, Provider, cciiodb, Cciio, Hios, Network, Identifier, Reference, InsurancePlan, SpdInsurancePlan, OrgAlias, ProviderRole, FhirCodeableConcept} from '../model/sequelizeModels';
import { issuerOrgMap } from './issuerMap';
import Sequelize from 'sequelize';

const Op = Sequelize.Op;

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
	async findParentOrg(){
		var orgWithParentQuery = "SELECT organization_id, partOf_organization_name FROM vhdir_organization where partOf_organization_name is not null and partOf_organization_name <> '' order by partOf_organization_name";
		var orgWithParent = await spddb.query(orgWithParentQuery, {model: Organization});
		var currentParent = '', currentParentId = 0;
		for (var i = 0; i < orgWithParent.length; i++){
			if (currentParent === orgWithParent[i].partOf_organization_name && currentParentId > 0) {
				Organization.update({
					partOf_organization_id: currentParentId
				}, {
					where: {
						organization_id: orgWithParent[i].organization_id
					}
				});				
			}
			else {
				currentParent = orgWithParent[i].partOf_organization_name
				var parentOrg = await Organization.findOne({where: {name: orgWithParent[i].partOf_organization_name}});
				if (parentOrg != null){
					console.log("Parent found: "+parentOrg.name);
					Organization.update({
						partOf_organization_id: parentOrg.organization_id
					}, {
						where: {
							organization_id: orgWithParent[i].organization_id
						}
					});
					currentParentId = parentOrg.organization_id;
				}
				else {
					console.log("Parent not found: "+orgWithParent[i].partOf_organization_name);currentParentId = 0;				
				}
			}
		}
	},
	async findParentOrgNpi(){
		var orgWithParentQuery = "SELECT organization_id, partOf_organization_name FROM vhdir_organization where partOf_organization_name is not null and partOf_organization_name <> '' and partOf_organization_id is null order by partOf_organization_name";
		var orgWithParent = await spddb.query(orgWithParentQuery, {model: Organization});
		var currentParent = '', currentParentId = 0;
		for (var i = 0; i < orgWithParent.length; i++){
			if (currentParent === orgWithParent[i].partOf_organization_name && currentParentId > 0) {
				Organization.update({
					partOf_organization_id: currentParentId
				}, {
					where: {
						organization_id: orgWithParent[i].organization_id
					}
				});				
			}
			else {
				
				currentParent = orgWithParent[i].partOf_organization_name;
				console.log("Look for parent: "+orgWithParent[i].partOf_organization_name);
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
				" and `Provider Organization Name (Legal Business Name)` = '"+
				orgWithParent[i].partOf_organization_name +
				"'";
				var res = await npidb.query(npiQuery, { model: Npi } );
				if (res != null && res.length > 0) {
					console.log("Parent found in NPI: "+res[0].organization_name);
					var isActive = '1';
					if (res[0].deactivation_date != null && res[0].deactivation_date.length > 4 
						&& (res[0].reactivation_date === null || res[0].reactivation_date.length <= 0)) {
						isActive = '0';
						//console.log("Org inactive: " + nameToUse);
					}
					var created = await Organization.create({
					  active: isActive,
					  name: res[0].organization_name,
					  partOf_organization_name: res[0].parent_name});
					//console.log("Org: "+JSON.stringify(created));
					var orgCreated = await Organization.findOne({where: {name: res[0].organization_name}});
					//console.log("OrgCreated: "+JSON.stringify(orgCreated));
					
					currentParentId = orgCreated.organization_id;
					Organization.update({
						partOf_organization_id: currentParentId
					}, {
						where: {
							organization_id: orgWithParent[i].organization_id
						}
					});				
					
					var idNpi = await Identifier.create({
						identifier_status_value_code: "active",
						use: "official",
						system: "http://hl7.org/fhir/sid/us-npi",
						value: res[0].NPI,
						organization_id: orgCreated.organization_id
					});	
					if (res[0].other_organization_name!= null && res[0].other_organization_name.length > 0){
						var alias1 = await OrgAlias.create({
							value: res[0].other_organization_name,
							organization_id: orgCreated.organization_id
						});
					}
					if (res[0].other_identifier_1!= null && res[0].other_identifier_1.length > 0){
						var alias1 = await OrgAlias.create({
							value: res[0].other_identifier_1,
							organization_id: orgCreated.organization_id
						});
					}
					var address1 = await Address.create({
						use: "billing",
						line1: res[0].mailing_address_first_line, 
						city: res[0].mailing_address_city, 
						state: res[0].mailing_address_state, 
						postalCode: res[0].mailing_address_postal_code,
						country: res[0].mailing_address_country_code,
						organization_id: orgCreated.organization_id
					});
					var address2 = await Address.create({					
						use: "work",
						line1: res[0].practice_location_first_line, 
						city: res[0].practice_location_city, 
						state: res[0].practice_location_state, 
						postalCode: res[0].practice_location_postal_code,
						country: res[0].practice_location_country_code,
						organization_id: orgCreated.organization_id
					});
					//console.log("address: "+JSON.stringify(address));
					var telecoms = await Telecom.create(
						{
						system: "phone",
						value: res[0].mailing_address_telephone,
						organization_id: orgCreated.organization_id
						});
					telecoms = await Telecom.create(
						{
						system: "fax",
						value: res[0].mailing_address_fax,
						organization_id: orgCreated.organization_id									
						});
					telecoms = await Telecom.create(
						{
						system: "phone",
						value: res[0].practice_location_telephone,
						organization_id: orgCreated.organization_id									
						});
					telecoms = await Telecom.create(
						{
						system: "fax",
						value: res[0].practice_location_fax,
						organization_id: orgCreated.organization_id									
						});
					//console.log("telecoms: "+JSON.stringify(telecoms));
					var name = await Name.create({
						use: "official",
						family: res[0].authorized_official_last_name,
						given: res[0].authorized_official_first_name,
					})
					//console.log("name: "+JSON.stringify(name));
					var nameCreated = await Name.findOne({where: {
						family: res[0].authorized_official_last_name,
						given: res[0].authorized_official_first_name}});
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
					value: res[0].authorized_official_telephone,
					organization_contact_id: contactCreated.organization_contact_id,
					organization_id: orgCreated.organization_id
					});

				}
				else {
					console.log("Parent not found in NPI: "+orgWithParent[i].partOf_organization_name);
					currentParentId = 0;
				}
			}
		}

	},
	async getInsurancePlans() {
		var plans = await InsurancePlan.findAll({
			order: [['PlanMarketingName']],
			offset: 0, 
			limit: 500});
		var currentName = '';
		for (var i = 0; i < plans.length; i++) {
			console.log("Plan Name: "+plans[i].PlanMarketingName);
			var ownerId = 0, refId = 0;
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
						system: "https://www.cms.gov/CCIIO/",
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
					var spdOrgEx = await Organization.findOne({where: {name: tempName}});
					if (spdOrgEx != null){
						var ref = await Reference.findOne({where:
							{
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: spdOrgEx.organization_id.toString()
							}
							});
						if (ref == null) {
							var tempRef = await Reference.create({
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: spdOrgEx.toString()
							});
							var refCreated = await Reference.findOne({where:
							{
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: spdOrgEx.toString()
							}
							});
							refId = refCreated.resource_reference_id
						}
						else {
							refId = ref.resource_reference_id;						
						}
						
					}
					else {
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
							system: "https://www.cms.gov/CCIIO/",
							value: hiosOrg.HIOS_ISSUER_ID,
							organization_id: orgCreated.organization_id
						});
						ownerId = orgCreated.organization_id;	
						//SPD-149
						var ref = await Reference.findOne({where:
							{
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: ownerId.toString()
							}
							});
						if (ref == null) {
							var tempRef = await Reference.create({
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: ownerId.toString()
							});
							var refCreated = await Reference.findOne({where:
							{
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: ownerId.toString()
							}
							});
							refId = refCreated.resource_reference_id
						}
						else {
							refId = ref.resource_reference_id;						
						}
					}
					
				}
				else {
					console.log("Owner not found: "+plans[i].PlanMarketingName+". Go to local map.");
					var orgFound = false;
					//SPD-117
					for (var j = 0; j < issuerOrgMap.length; j++) {
						if (plans[i].IssuerId == issuerOrgMap[j].issuerId){
							orgFound = true;
							var orgExisted = await Organization.findOne({where: {name: issuerOrgMap[j].orgName}});
							if (orgExisted == null) {
								var created = await Organization.create({
								  active: '1',
								  name: issuerOrgMap[j].orgName});
								var orgCreated = await Organization.findOne({where: {name: issuerOrgMap[j].orgName}});
								var id = await Identifier.create({
									identifier_status_value_code: "active",
									use: "secondary",
									system: "https://www.cms.gov/CCIIO/",
									value: issuerOrgMap[j].issuerId,
									organization_id: orgCreated.organization_id
								});
								var ref = await Reference.findOne({where:
									{
										type: 'vhdir_organization',
										identifier: orgCreated.organization_id.toString()
									}
									});
								if (ref == null) {
									var tempRef = await Reference.create({
										type: 'vhdir_organization',
										identifier: orgCreated.organization_id.toString()
									});
									var refCreated = await Reference.findOne({where:
									{
										type: 'vhdir_organization',
										identifier: orgCreated.organization_id.toString()
									}
									});
									refId = refCreated.resource_reference_id
								}
								else {
									refId = ref.resource_reference_id;						
								}
								
							}
							else {
								var refEx = await Reference.findOne({where:
									{
										type: 'vhdir_organization',
										identifier: orgExisted.organization_id.toString()
									}
									});
								refId = refEx.resource_reference_id
							}
						}
					}
					if (!orgFound)
						continue;
				}
			}
			else {
				var refExisted = await Reference.findOne({where:
					{
						type: 'vhdir_organization',
						//identifier: orgName
						identifier: id.organization_id.toString()
					}
					});
					
				refId = refExisted.resource_reference_id;

				//ownerId = id.organization_id;
			}
			var plan = await SpdInsurancePlan.create({
				status: 'active',
				name: plans[i].PlanMarketingName,
				period_start: plans[i].PlanEffectiveDate,
				period_end: plans[i].PlanExpirationDate,
				ownedBy_reference_id: refId,
				administeredBy_reference_id: refId
			});
			var planCreated = await SpdInsurancePlan.findOne({
				where: {
					name: plans[i].PlanMarketingName
				}
			});
			var id = await Identifier.create({
				identifier_status_value_code: "active",
				use: "official",
				system: "https://www.cms.gov/CCIIO/",
				value: plans[i].PlanId,
				insurance_plan_id: planCreated.insurance_plan_id
			});											
			
		}
	},
	async getNetworks() {
		var cciioQuery = "SELECT NetworkId, SourceName, IssuerId,"+
			" NetworkName, StateCode FROM cciio.network"+
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
						orgId = org.organization_id.toString();
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
						//SPD-146
						var id = await Identifier.create({
							identifier_status_value_code: "active",
							use: "secondary",
							system: "https://www.cms.gov/CCIIO/",
							value: owner.HIOS_ISSUER_ID,
							organization_id: orgCreated.organization_id
						});											
						orgId = orgCreated.organization_id.toString();
						orgName = orgCreated.name;
					}
				}
				if (names.length > 0) {
					var refId = null;
					//SPD-150
					var ref = await Reference.findOne({where:
						{
							type: 'vhdir_organization',
							//identifier: orgName
							identifier: orgId
						}
						});
					if (ref == null) {
						var tempRef = await Reference.create({
							type: 'vhdir_organization',
							//identifier: orgName
							identifier: orgId
						});
						var refCreated = await Reference.findOne({where:
						{
							type: 'vhdir_organization',
							//identifier: orgName
							identifier: orgId
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
					//SPD-146
					var id = await Identifier.create({
						identifier_status_value_code: "active",
						use: "official",
						system: "https://www.cms.gov/CCIIO/",
						value: nwId,
						network_id: nwCreated.network_id
					});	
					//SPD-145	
					var addressNw = await Address.create({
						use: "work",
						state: res[i].StateCode,
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
        "`Provider First Line Business Practice Location Address` limit 3900, 10000";
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
				}*/
				var telecoms = null;
				if (res[i].mailing_address_telephone != null && res[i].mailing_address_telephone.length > 0) {
					telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].mailing_address_telephone,
					use: "work",
					rank: 1,
					organization_id: exist.organization_id
					});
				}
				if (res[i].mailing_address_fax != null && res[i].mailing_address_fax.length > 0) {
				telecoms = await Telecom.create(
					{
					system: "fax",
					use: "work",
					rank: 1,
					value: res[i].mailing_address_fax,
					organization_id: exist.organization_id									
					});
				}
				if (res[i].practice_location_telephone != null && res[i].practice_location_telephone.length > 0) {
				telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].practice_location_telephone,
					use: "work",
					rank: 2,
					organization_id: exist.organization_id									
					});
				}
				if (res[i].practice_location_fax != null && res[i].practice_location_fax.length > 0) {
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].practice_location_fax,
					use: "work",
					rank: 2,
					organization_id: exist.organization_id									
					});
				}
				/*var id = await Identifier.create({
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
			var telecoms = null;
			if (res[i].mailing_address_telephone != null && res[i].mailing_address_telephone.length > 0) {
				telecoms = await Telecom.create(
				{
				system: "phone",
				value: res[i].mailing_address_telephone,
				use: "work",
				rank: 1,
				organization_id: orgCreated.organization_id
				});
			}
			if (res[i].mailing_address_fax != null && res[i].mailing_address_fax.length > 0) {
			telecoms = await Telecom.create(
				{
				system: "fax",
				use: "work",
				rank: 1,
				value: res[i].mailing_address_fax,
				organization_id: orgCreated.organization_id									
				});
			}
			if (res[i].practice_location_telephone != null && res[i].practice_location_telephone.length > 0) {
			telecoms = await Telecom.create(
				{
				system: "phone",
				value: res[i].practice_location_telephone,
				use: "work",
				rank: 2,
				organization_id: orgCreated.organization_id									
				});
			}
			if (res[i].practice_location_fax != null && res[i].practice_location_fax.length > 0) {
			telecoms = await Telecom.create(
				{
				system: "fax",
				value: res[i].practice_location_fax,
				use: "work",
				rank: 2,
				organization_id: orgCreated.organization_id									
				});
			}
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
		"`Healthcare Provider Taxonomy Code_2` as healthcare_taxonomy_code_2, "+
		"`Healthcare Provider Taxonomy Code_3` as healthcare_taxonomy_code_3, "+
		"`Healthcare Provider Taxonomy Code_4` as healthcare_taxonomy_code_4, "+
		"`Healthcare Provider Taxonomy Code_5` as healthcare_taxonomy_code_5, "+
		"`Healthcare Provider Taxonomy Code_6` as healthcare_taxonomy_code_6, "+
		"`Healthcare Provider Taxonomy Code_7` as healthcare_taxonomy_code_7, "+
		"`Healthcare Provider Taxonomy Code_8` as healthcare_taxonomy_code_8, "+
		"`Healthcare Provider Taxonomy Code_9` as healthcare_taxonomy_code_9, "+
		"`Healthcare Provider Taxonomy Code_10` as healthcare_taxonomy_code_10, "+
		"`Healthcare Provider Taxonomy Code_11` as healthcare_taxonomy_code_11, "+
		"`Healthcare Provider Taxonomy Code_12` as healthcare_taxonomy_code_12, "+
		"`Healthcare Provider Taxonomy Code_13` as healthcare_taxonomy_code_13, "+
		"`Healthcare Provider Taxonomy Code_14` as healthcare_taxonomy_code_14, "+
		"`Healthcare Provider Taxonomy Code_15` as healthcare_taxonomy_code_15, "+
		"`Provider License Number_1` as license_num_1, "+
		"`Provider License Number State Code_1` as license_num_state_1, "+
		"`Other Provider Identifier_1` as other_identifier_1,"+
		"`Other Provider Identifier Type Code_1` as other_identifier_type_code_1,"+
		"`Other Provider Identifier State_1` as other_identifier_state_1,"+
		"`Other Provider Identifier Issuer_1` as other_identifier_issuer_1,"+
		"`Is Sole Proprietor` as is_sole_proprietor "+
		"FROM nppes.npi WHERE `Entity Type Code` = 1 "+
		"order by `Provider Last Name (Legal Name)`, `Provider First Name`, `Provider Middle Name` "+
		"limit 12000, 2000";
		var res = await npidb.query(providerQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		for (var i = 0; i < res.length; i++) {
			/*for (var k = 1; k < 16; k++) {
			var codePos = eval("res[i].healthcare_taxonomy_code_"+k);
			if (codePos != null && codePos.length > 0 && k > 1) {
				console.log("Name: "+res[i].last_name+" Role position "+k+": "+codePos);
				}
			}
			continue;*/

			var fullName = res[i].last_name+res[i].first_name+res[i].middle_name;
			//console.log("full name: "+JSON.stringify(fullName));
			var provider = await Provider.findOne({where: {photo: fullName}});
			//console.log("provider: "+JSON.stringify(provider));
			if (provider != null){
				console.log("provider found "+provider.photo);
				/*var idNpi = await Identifier.create({
					identifier_status_value_code: "active",
					use: "official",
					system: "http://hl7.org/fhir/sid/us-npi",
					value: res[i].NPI,
					practitioner_id: provider.practitioner_id
				});	
				var givenName = res[i].first_name;				
				if (res[i].middle_name != null && res[i].middle_name.length > 0){
					givenName +=  " " + res[i].middle_name;
				}
				var name1 = await Name.update({
					given: givenName,
					prefix: res[i].prefix,
					suffix: res[i].suffix
				}, {
					where: {
						practitioner_id: provider.practitioner_id,
						use: "official"											
					}
				});
				if ((res[i].other_last_name != null && res[i].other_last_name.length > 0)
					|| (res[i].other_first_name != null && res[i].other_first_name.length > 0)) { 
					givenName = res[i].other_first_name;
					if (res[i].other_middle_name != null && res[i].other_middle_name.length > 0){
						givenName +=  " " + res[i].other_middle_name;
					}
					var name2 = await Name.update({
						given: givenName,
						prefix: res[i].other_prefix,
						suffix: res[i].other_suffix
					}, {
					where: {
						practitioner_id: provider.practitioner_id,
						use: "usual"											
					}
				});
				}*/
				continue;
			}
			if (provider == null) {
				var isActive = '1';
				if (res[i].deactivation_date != null && res[i].deactivation_date.length > 4 
					&& (res[i].reactivation_date === null || res[i].reactivation_date.length <= 0)) {
					isActive = '0';
					console.log("Provider inactive: " + fullName);
				}
				var genderCode = 'unknown';
				if (res[i].gender === 'F') {
					genderCode = 'female'					
				}
				else if (res[i].gender === 'M') {
					genderCode = 'male'					
				}
					
				var created = await Provider.create({
					active: isActive,
					gender: genderCode,
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
				//SPD-140
				var orgAddress = await Address.findOne({where: 
					{	
						use: "work",
						line1: res[i].practice_location_first_line, 
						city: res[i].practice_location_city, 
						state: res[i].practice_location_state, 
						postalCode: res[i].practice_location_postal_code,
						country: res[i].practice_location_country_code,
						organization_id: {
											[Op.ne]: null
										 }
					}
				});
				if (orgAddress != null) {
					console.log("Role org found: "+orgAddress.organization_id);
					var role = await ProviderRole.create({
						active: '1',
						practitioner_id: providerCreated.practitioner_id,
						organization_id: orgAddress.organization_id						
					});
					var roleCreated = await ProviderRole.findOne({where:
					{
						practitioner_id: providerCreated.practitioner_id,
						organization_id: orgAddress.organization_id	
					}						
					});
					var codePos = null;
					for (var j = 1; j < 16; j++) {
						codePos = eval("res[i].healthcare_taxonomy_code_"+j);
						console.log("Role position: "+codePos);
						if (codePos != null && codePos.length > 0) {
							var codeCreated = await FhirCodeableConcept.create({
								text: "Taxonomy Code",
								coding_code: codePos,
								practitioner_role_specialty_id: roleCreated.practitioner_role_id,
								practitioner_role_code_id: roleCreated.practitioner_role_id
							})
						}
						
					}
					
					
				}
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
				var telecoms = null;
				if (res[i].mailing_address_telephone != null && res[i].mailing_address_telephone.length > 0) {
					telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].mailing_address_telephone,
					use: "work",
					rank: 1,
					practitioner_id: providerCreated.practitioner_id
					});
				}
				if (res[i].mailing_address_fax != null && res[i].mailing_address_fax.length > 0) {
				telecoms = await Telecom.create(
					{
					system: "fax",
					use: "work",
					rank: 1,
					value: res[i].mailing_address_fax,
					practitioner_id: providerCreated.practitioner_id									
					});
				}
				if (res[i].practice_location_telephone != null && res[i].practice_location_telephone.length > 0) {
				telecoms = await Telecom.create(
					{
					system: "phone",
					value: res[i].practice_location_telephone,
					use: "work",
					rank: 2,
					practitioner_id: providerCreated.practitioner_id									
					});
				}
				if (res[i].practice_location_fax != null && res[i].practice_location_fax.length > 0) {
				telecoms = await Telecom.create(
					{
					system: "fax",
					value: res[i].practice_location_fax,
					use: "work",
					rank: 2,
					practitioner_id: providerCreated.practitioner_id									
					});
				}
				//console.log("telecoms: "+JSON.stringify(telecoms));
				var givenName = res[i].first_name;				
				if (res[i].middle_name != null && res[i].middle_name.length > 0){
					givenName +=  " " + res[i].middle_name;
				}
				var name1 = await Name.create({
					use: "official",
					family: res[i].last_name,
					given: givenName,
					prefix: res[i].prefix,
					suffix: res[i].suffix,
					practitioner_id: providerCreated.practitioner_id									
				});
				if ((res[i].other_last_name != null && res[i].other_last_name.length > 0)
					|| (res[i].other_first_name != null && res[i].other_first_name.length > 0)) { 
					givenName = res[i].other_first_name;
					if (res[i].other_middle_name != null && res[i].other_middle_name.length > 0){
						givenName +=  " " + res[i].other_middle_name;
					}
					var name2 = await Name.create({
						use: "usual",
						family: res[i].other_last_name,
						given: givenName,
						prefix: res[i].other_prefix,
						suffix: res[i].other_suffix,
						practitioner_id: providerCreated.practitioner_id									
					});
				}
			}
		}

	}
};