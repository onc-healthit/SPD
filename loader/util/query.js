import { Npi, npidb, spddb, Organization, Address, Telecom, Contact, Name, Provider, cciiodb, Cciio, Hios, Network, Identifier, Reference, InsurancePlan, SpdInsurancePlan, OrgAlias, ProviderRole, FhirCodeableConcept, spdHealthcareSvc, SpdRestriction, SpdValidation, SpdLocation} from '../model/sequelizeModels';
import { issuerOrgMap } from './issuerMap';
import Sequelize from 'sequelize';

const Op = Sequelize.Op;

export const query = {
	//SPD-164
	async updateHealthcareService(){
		var healthcareOrgQuery = "SELECT healthcare_service_id FROM vhdir_healthcare_service where name = 'Hospital' limit 0, 5000";
		console.log("service start: "+ Date.now());
		
		var healthcareHospitalOrg = await spddb.query(healthcareOrgQuery, {model: spdHealthcareSvc});
		if (healthcareHospitalOrg != null) {
			var svcHospitalCatRecords = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-category.html",
						coding_code: "35",
						coding_display: "Hospital",
						healthcare_service_category_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalCatCreated = await FhirCodeableConcept.bulkCreate(svcHospitalCatRecords);
			
			var svcHospitalType1Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "165",
						coding_display: "Cardiology",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType1Created = await FhirCodeableConcept.bulkCreate(svcHospitalType1Records);

			var svcHospitalType2Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "170",
						coding_display: "Gastroenterology & Hepatology",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType2Created = await FhirCodeableConcept.bulkCreate(svcHospitalType2Records);
			
			var svcHospitalType3Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "172",
						coding_display: "Immunology & Allergy",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType3Created = await FhirCodeableConcept.bulkCreate(svcHospitalType3Records);
			
			var svcHospitalType4Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "173",
						coding_display: "Infectious Diseases",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType4Created = await FhirCodeableConcept.bulkCreate(svcHospitalType4Records);
			
			var svcHospitalType5Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "186",
						coding_display: "Obstetrics & Gynaecology",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType5Created = await FhirCodeableConcept.bulkCreate(svcHospitalType5Records);
			
			var svcHospitalType6Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "191",
						coding_display: "Paediatric Clinical Genetics",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType6Created = await FhirCodeableConcept.bulkCreate(svcHospitalType6Records);
			
			var svcHospitalType7Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "209",
						coding_display: "Diag. Radiology /Xray /CT /Fluoroscopy",
						healthcare_service_type_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var svcHospitalType7Created = await FhirCodeableConcept.bulkCreate(svcHospitalType7Records);
			
			var specialtyHospital1Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394579002",
						coding_display: "Cardiology",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital1Created = await FhirCodeableConcept.bulkCreate(specialtyHospital1Records);
			
			var specialtyHospital2Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394584008",
						coding_display: "Gastroenterology",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital2Created = await FhirCodeableConcept.bulkCreate(specialtyHospital2Records);

			var specialtyHospital3Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394598000",
						coding_display: "Immunopathology",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital3Created = await FhirCodeableConcept.bulkCreate(specialtyHospital3Records);
			
			var specialtyHospital4Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394807007",
						coding_display: "Infectious diseases",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital4Created = await FhirCodeableConcept.bulkCreate(specialtyHospital4Records);
			
			var specialtyHospital5Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394586005",
						coding_display: "Gynecology",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital5Created = await FhirCodeableConcept.bulkCreate(specialtyHospital5Records);
			
			var specialtyHospital6Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "420208008",
						coding_display: "Pediatric genetics",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital6Created = await FhirCodeableConcept.bulkCreate(specialtyHospital6Records);
			
			var specialtyHospital7Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394914008",
						coding_display: "Radiology",
						healthcare_service_specialty_id: healthcareHospitalOrg[i].healthcare_service_id
				}));		
			var specialtyHospital7Created = await FhirCodeableConcept.bulkCreate(specialtyHospital7Records);
			
		}

		console.log("service end: "+ Date.now());		
	},
	async getHealthcareService(){
		var healthcareOrgQuery = "SELECT organization_id, name FROM vhdir_organization where name like '%";
		var dental = "DENTAL%'";
		var complementary = "CHIROPRACTIC%'";
		var hospital = "HOSPITAL%'";
		var emergency = "URGENT CARE%'";
		console.log("service start: "+ Date.now());
		var healthcareDentalOrg = await spddb.query(healthcareOrgQuery+dental, {model: Organization});
		if (healthcareDentalOrg != null) {
			console.log("Dental org found: "+ healthcareDentalOrg.length );
			var healthcareDentalOrgRecords = healthcareDentalOrg.map((x, i) => ({
			active: '1', 
			name: 'Dental Care Service', 
			provided_by_organization_id: healthcareDentalOrg[i].organization_id
			}));		
		
			var svcCreated = await spdHealthcareSvc.bulkCreate(healthcareDentalOrgRecords);
			
			var svcCatRecords = healthcareDentalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-category.html",
						coding_code: "10",
						coding_display: "Dental",
						healthcare_service_category_id: svcCreated[i].healthcare_service_id
				}));		
			var svcCatCreated = await FhirCodeableConcept.bulkCreate(svcCatRecords);
			
			var svcType1Records = healthcareDentalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "88",
						coding_display: "General Dental",
						healthcare_service_type_id: svcCreated[i].healthcare_service_id
				}));		
			var svcType1Created = await FhirCodeableConcept.bulkCreate(svcType1Records);

			var svcType2Records = healthcareDentalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "92",
						coding_display: "Paediatric Dentistry",
						healthcare_service_type_id: svcCreated[i].healthcare_service_id
				}));		
			var svcType2Created = await FhirCodeableConcept.bulkCreate(svcType2Records);
			
			var svcType3Records = healthcareDentalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "89",
						coding_display: "Oral Medicine",
						healthcare_service_type_id: svcCreated[i].healthcare_service_id
				}));		
			var svcType3Created = await FhirCodeableConcept.bulkCreate(svcType3Records);
			
			var specialty1Records = healthcareDentalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "408444009",
						coding_display: "Dental-General dental practice",
						healthcare_service_specialty_id: svcCreated[i].healthcare_service_id
				}));		
			var specialty1Created = await FhirCodeableConcept.bulkCreate(specialty1Records);
			
			var specialty2Records = healthcareDentalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394812008",
						coding_display: "Dental medicine specialties",
						healthcare_service_specialty_id: svcCreated[i].healthcare_service_id
				}));		
			var specialty2Created = await FhirCodeableConcept.bulkCreate(specialty2Records);

			var specialty3Records = healthcareDentalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394607009",
						coding_display: "Pediatric dentistry",
						healthcare_service_specialty_id: svcCreated[i].healthcare_service_id
				}));		
			var specialty3Created = await FhirCodeableConcept.bulkCreate(specialty3Records);
		}

		var healthcareChiroOrg = await spddb.query(healthcareOrgQuery+complementary, {model: Organization});
		if (healthcareChiroOrg != null) {
			console.log("Chiro org found: "+ healthcareChiroOrg.length );
			var healthcareChiroOrgRecords = healthcareChiroOrg.map((x, i) => ({
			active: '1', 
			name: 'Alternative & Complementary Therapies', 
			provided_by_organization_id: healthcareChiroOrg[i].organization_id
			}));				
			var svcChiroCreated = await spdHealthcareSvc.bulkCreate(healthcareChiroOrgRecords);
			
			var svcChiroCatRecords = healthcareChiroOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-category.html",
						coding_code: "3",
						coding_display: "Alternative/Complementary Therapies",
						healthcare_service_category_id: svcChiroCreated[i].healthcare_service_id
				}));		
			var svcChiroCatCreated = await FhirCodeableConcept.bulkCreate(svcChiroCatRecords);
			
			var svcChiroType1Records = healthcareChiroOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "52",
						coding_display: "Chiropractic",
						healthcare_service_type_id: svcChiroCreated[i].healthcare_service_id
				}));		
			var svcChiroType1Created = await FhirCodeableConcept.bulkCreate(svcChiroType1Records);

			var specialtyChiro1Records = healthcareChiroOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394733009",
						coding_display: "Medical specialty--OTHER--NOT LISTED",
						healthcare_service_specialty_id: svcChiroCreated[i].healthcare_service_id
				}));		
			var specialtyChiro1Created = await FhirCodeableConcept.bulkCreate(specialtyChiro1Records);			
		}
		
		var healthcareHospitalOrg = await spddb.query(healthcareOrgQuery+hospital, {model: Organization});
		if (healthcareHospitalOrg != null) {
			console.log("Hospital org found: "+ healthcareHospitalOrg.length );
			var healthcareHospitalOrgRecords = healthcareHospitalOrg.map((x, i) => ({
			active: '1', 
			name: 'Hospital', 
			provided_by_organization_id: healthcareHospitalOrg[i].organization_id
			}));		
		
			var svcHospitalCreated = await spdHealthcareSvc.bulkCreate(healthcareHospitalOrgRecords);
			
			var svcHospitalCatRecords = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-category.html",
						coding_code: "35",
						coding_display: "Hospital",
						healthcare_service_category_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalCatCreated = await FhirCodeableConcept.bulkCreate(svcHospitalCatRecords);
			
			var svcHospitalType1Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "165",
						coding_display: "Cardiology",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType1Created = await FhirCodeableConcept.bulkCreate(svcHospitalType1Records);

			var svcHospitalType2Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "170",
						coding_display: "Gastroenterology & Hepatology",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType2Created = await FhirCodeableConcept.bulkCreate(svcHospitalType2Records);
			
			var svcHospitalType3Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "172",
						coding_display: "Immunology & Allergy",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType3Created = await FhirCodeableConcept.bulkCreate(svcHospitalType3Records);
			
			var svcHospitalType4Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "173",
						coding_display: "Infectious Diseases",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType4Created = await FhirCodeableConcept.bulkCreate(svcHospitalType4Records);
			
			var svcHospitalType5Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "186",
						coding_display: "Obstetrics & Gynaecology",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType5Created = await FhirCodeableConcept.bulkCreate(svcHospitalType5Records);
			
			var svcHospitalType6Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "191",
						coding_display: "Paediatric Clinical Genetics",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType6Created = await FhirCodeableConcept.bulkCreate(svcHospitalType6Records);
			
			var svcHospitalType7Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "209",
						coding_display: "Diag. Radiology /Xray /CT /Fluoroscopy",
						healthcare_service_type_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var svcHospitalType7Created = await FhirCodeableConcept.bulkCreate(svcHospitalType7Records);
			
			var specialtyHospital1Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394579002",
						coding_display: "Cardiology",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital1Created = await FhirCodeableConcept.bulkCreate(specialtyHospital1Records);
			
			var specialtyHospital2Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394584008",
						coding_display: "Gastroenterology",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital2Created = await FhirCodeableConcept.bulkCreate(specialtyHospital2Records);

			var specialtyHospital3Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394598000",
						coding_display: "Immunopathology",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital3Created = await FhirCodeableConcept.bulkCreate(specialtyHospital3Records);
			
			var specialtyHospital4Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394807007",
						coding_display: "Infectious diseases",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital4Created = await FhirCodeableConcept.bulkCreate(specialtyHospital4Records);
			
			var specialtyHospital5Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394586005",
						coding_display: "Gynecology",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital5Created = await FhirCodeableConcept.bulkCreate(specialtyHospital5Records);
			
			var specialtyHospital6Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "420208008",
						coding_display: "Pediatric genetics",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital6Created = await FhirCodeableConcept.bulkCreate(specialtyHospital6Records);
			
			var specialtyHospital7Records = healthcareHospitalOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "394914008",
						coding_display: "Radiology",
						healthcare_service_specialty_id: svcHospitalCreated[i].healthcare_service_id
				}));		
			var specialtyHospital7Created = await FhirCodeableConcept.bulkCreate(specialtyHospital7Records);
			
		}

		var healthcareUrgentOrg = await spddb.query(healthcareOrgQuery+emergency, {model: Organization});
		if (healthcareUrgentOrg != null) {
			console.log("Urgent org found: "+ healthcareUrgentOrg.length );
			var healthcareUrgentOrgRecords = healthcareUrgentOrg.map((x, i) => ({
			active: '1', 
			name: 'Emergency Department', 
			provided_by_organization_id: healthcareUrgentOrg[i].organization_id
			}));		
		
			var svcUrgentCreated = await spdHealthcareSvc.bulkCreate(healthcareUrgentOrgRecords);
			
			var svcUrgentCatRecords = healthcareUrgentOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-category.html",
						coding_code: "14",
						coding_display: "Emergency Department",
						healthcare_service_category_id: svcUrgentCreated[i].healthcare_service_id
				}));		
			var svcUrgentCatCreated = await FhirCodeableConcept.bulkCreate(svcUrgentCatRecords);
			
			var svcUrgentType1Records = healthcareUrgentOrg.map((x, i) => ({
						coding_system: "https://www.hl7.org/fhir/codesystem-service-type.html",
						coding_code: "117",
						coding_display: "Emergency Medical",
						healthcare_service_type_id: svcUrgentCreated[i].healthcare_service_id
				}));		
			var svcUrgentType1Created = await FhirCodeableConcept.bulkCreate(svcUrgentType1Records);
			
			var specialtyUrgent1Records = healthcareUrgentOrg.map((x, i) => ({
						coding_system: "http://www.snomed.org/",
						coding_code: "408478003",
						coding_display: "Critical care medicine",
						healthcare_service_specialty_id: svcUrgentCreated[i].healthcare_service_id
				}));		
			var specialtyUrgent1Created = await FhirCodeableConcept.bulkCreate(specialtyUrgent1Records);
		}
		console.log("service end: "+ Date.now());		
	},
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
		console.log("parent start: "+ Date.now());
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
		console.log("parent end: "+ Date.now());
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
					/*if (res[0].other_identifier_1!= null && res[0].other_identifier_1.length > 0){
						var alias1 = await OrgAlias.create({
							value: res[0].other_identifier_1,
							organization_id: orgCreated.organization_id
						});
					}*/
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
					contact_id: contactCreated.contact_id,
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
		console.log("plan start: "+ Date.now());

		var plans = await InsurancePlan.findAll({
			order: [['PlanMarketingName']]
		});
		var currentName = '';
		for (var i = 0; i < plans.length; i++) {
			//console.log("Plan Name: "+plans[i].PlanMarketingName);
			var ownerId = 0, refId = 0;
			if (plans[i].PlanMarketingName === currentName) {
				//console.log("Plan exist: "+plans[i].PlanMarketingName);
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
							var refCreated = await Reference.create({
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: spdOrgEx.toString()
							});
							/*var refCreated = await Reference.findOne({where:
							{
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: spdOrgEx.toString()
							}
							});*/
							refId = refCreated.resource_reference_id
						}
						else {
							refId = ref.resource_reference_id;						
						}
						
					}
					else {
						var orgCreated = await Organization.create({
						  active: '1',
						  name: tempName});
						//var orgCreated = await Organization.findOne({where: {name: tempName}});
						if (hiosOrg.ORG_STATE.length <= 2) {
						//console.log("Good State: "+hiosOrg.ORG_STATE);
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
						//console.log("Bad State: "+hiosOrg.ORG_STATE);
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
							var refCreated = await Reference.create({
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: ownerId.toString()
							});
							/*var refCreated = await Reference.findOne({where:
							{
								type: 'vhdir_organization',
								//identifier: orgName
								identifier: ownerId.toString()
							}
							});*/
							refId = refCreated.resource_reference_id
						}
						else {
							refId = ref.resource_reference_id;						
						}
					}
					
				}
				else {
					//console.log("Owner not found: "+plans[i].PlanMarketingName+". Go to local map.");
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
									var refCreated = await Reference.create({
										type: 'vhdir_organization',
										identifier: orgCreated.organization_id.toString()
									});
									/*var refCreated = await Reference.findOne({where:
									{
										type: 'vhdir_organization',
										identifier: orgCreated.organization_id.toString()
									}
									});*/
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
			var planCreated = await SpdInsurancePlan.create({
				status: 'active',
				name: plans[i].PlanMarketingName,
				period_start: plans[i].PlanEffectiveDate,
				period_end: plans[i].PlanExpirationDate,
				ownedBy_reference_id: refId,
				administeredBy_reference_id: refId
			});
			/*var planCreated = await SpdInsurancePlan.findOne({
				where: {
					name: plans[i].PlanMarketingName
				}
			});*/
			//SPD-174
			var network = await Identifier.findOne({
				where: {
					value: plans[i].NetworkId,
					network_id: {
									[Op.ne]: null
								}
				}
			});
			if (network != null) {
				var tempRef = await Reference.create({
									type: 'vhdir_network',
									identifier: network.network_id.toString(),
									insurance_plan_network_id: planCreated.insurance_plan_id
						});				
			}
			var id = await Identifier.create({
				identifier_status_value_code: "active",
				use: "official",
				system: "https://www.cms.gov/CCIIO/",
				value: plans[i].PlanId,
				insurance_plan_id: planCreated.insurance_plan_id
			});											
			
		}			
		console.log("plan end: "+ Date.now());

	},
	async getNetworks() {
		var cciioQuery = "SELECT NetworkId, SourceName, IssuerId,"+
			" NetworkName, StateCode FROM cciio.network"+
			" WHERE SourceName='HIOS' order by NetworkId ";
			console.log("network start: "+ Date.now());
		var res = await cciiodb.query(cciioQuery, { model: Cciio } );
		var nwId = '', names = [], owner = null, org = null, orgId = null, orgName = null ;
		for (var i = 0; i < res.length; i++) {
			if (res[i].NetworkId != nwId){
				owner = await Hios.findOne({where: {HIOS_ISSUER_ID: res[i].IssuerId}}); 
				if (owner != null) {
					org = await Organization.findOne({where: {name: owner.ISSR_LGL_NAME}});
					if (org != null) {
						//console.log("Org Found: "+JSON.stringify(org));
						orgId = org.organization_id.toString();
						orgName = org.name;
					}
					else {
						var tempName = 'EMPTY';
						if (owner.ISSR_LGL_NAME != null && owner.ISSR_LGL_NAME.length > 0){
							tempName = owner.ISSR_LGL_NAME;
						}
						var orgCreated = await Organization.create({
						  active: '1',
						  name: tempName});
						//console.log("Org: "+JSON.stringify(created));
						//var orgCreated = await Organization.findOne({where: {name: tempName}});
						//console.log("OrgCreated: "+JSON.stringify(orgCreated));
						if (owner.ORG_STATE.length <= 2) {
							//console.log("Good State: "+owner.ORG_STATE);
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
							//console.log("Bad State: "+owner.ORG_STATE);
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
						var refCreated = await Reference.create({
							type: 'vhdir_organization',
							//identifier: orgName
							identifier: orgId
						});
						/*var refCreated = await Reference.findOne({where:
						{
							type: 'vhdir_organization',
							//identifier: orgName
							identifier: orgId
						}
						});*/
						refId = refCreated.resource_reference_id
					}
					else {
						refId = ref.resource_reference_id;						
					}
					var nwCreated = await Network.create({
						active: '1',
						alias: names.join(";"),
						part_of_resource_reference_id: refId
					});
					//var nwCreated = await Network.findOne({where: {alias: names.join(";")}});
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
		console.log("network end: "+ Date.now());

	},
	async getOrgs() {
	for (var iteration = 0; iteration < 132; iteration++) {
			console.log("iteration "+ iteration + " start at "+ Date.now());
			var limit = iteration * 10000;
			//console.log("limit "+ limit);
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
        "`Provider First Line Business Practice Location Address` limit "+
		limit +", 10000";
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
				//console.log("Org Found" + exist.organization_id);
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
					contact_id: contactExisting.contact_id,
					organization_id: exist.organization_id
					});
				}*/
				/*var telecoms = null;
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
				}*/
				/*var id = await Identifier.create({
					identifier_status_value_code: "active",
					use: "npi",
					value: res[i].NPI,
					organization_id: exist.organization_id
				});	*/
				continue;
			}
			//console.log("Org Not Found: to create " + nameToUse)
			var isActive = '1';
			if (res[i].deactivation_date != null && res[i].deactivation_date.length > 4 
				&& (res[i].reactivation_date === null || res[i].reactivation_date.length <= 0)) {
				isActive = '0';
				//console.log("Org inactive: " + nameToUse);
			}
			var orgCreated = await Organization.create({
			  active: isActive,
			  name: nameToUse,
			partOf_organization_name: res[i].parent_name});
			//console.log("Org: "+JSON.stringify(orgCreated));
			//var orgCreated = await Organization.findOne({where: {name: nameToUse}});
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
			var nameCreated = await Name.create({
				use: "official",
				family: res[i].authorized_official_last_name,
				given: res[i].authorized_official_first_name,
			})
			//console.log("name: "+JSON.stringify(nameCreated));
			/*var nameCreated = await Name.findOne({where: {
				family: res[i].authorized_official_last_name,
				given: res[i].authorized_official_first_name}});*/
			//console.log("nameCreated: "+JSON.stringify(nameCreated));
			var contactCreated = await Contact.create({
				name_id: nameCreated.name_id,
				organization_id: orgCreated.organization_id
			})
			//console.log("Contact: "+JSON.stringify(contactCreated));
			/*var contactCreated = await Contact.findOne({where: {
				name_id: nameCreated.name_id,
				organization_id: orgCreated.organization_id				
			}});*/
			var telecoms = await Telecom.create(
			{
			system: "phone",
			value: res[i].authorized_official_telephone,
			contact_id: contactCreated.contact_id,
			organization_id: orgCreated.organization_id
			});
			
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
		"limit 2200, 4000";
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
					
				var providerCreated = await Provider.create({
					active: isActive,
					gender: genderCode,
					photo: res[i].last_name+res[i].first_name+res[i].middle_name});
				//console.log("Provider: "+JSON.stringify(created));
				//var providerCreated = await Provider.findOne({where: {photo: res[i].last_name+res[i].first_name+res[i].middle_name}});
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
					var roleCreated = await ProviderRole.create({
						active: '1',
						practitioner_id: providerCreated.practitioner_id,
						organization_id: orgAddress.organization_id						
					});
					/*var roleCreated = await ProviderRole.findOne({where:
					{
						practitioner_id: providerCreated.practitioner_id,
						organization_id: orgAddress.organization_id	
					}						
					});*/
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

	},
	async getBulkOrgs() {
	for (var iteration = 0; iteration < 132; iteration++) {
			console.log("iteration "+ iteration + " start at "+ Date.now());
			var limit = iteration * 10000;
			console.log("limit "+ limit);
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
        "`Provider First Line Business Practice Location Address` limit "+
		limit +", 10000";
		//"0, 10000";
		console.log("bulk start: "+ Date.now());
		var res = await npidb.query(npiQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		var orgRecords = res.map((x, i) => ({
			active: '1', 
			name: res[i].organization_name, 
			partOf_organization_name: res[i].parent_name})
			);
		var orgCreated = await Organization.bulkCreate(orgRecords);
		
		var idRecords = res.map((x, i) => ({
				identifier_status_value_code: "active",
				use: "official",
				system: "http://hl7.org/fhir/sid/us-npi",
				value: res[i].NPI,
				organization_id: orgCreated[i].organization_id})
			);
		var idCreated = await Identifier.bulkCreate(idRecords);
		
		var oaRecords = res.map((x, i) => ({
				value: res[i].other_organization_name,
				organization_id: orgCreated[i].organization_id})
			);
		var oaCreated = await OrgAlias.bulkCreate(oaRecords);
		
		var addressRecords = res.map((x, i) => ({
				use: "billing",
				line1: res[i].mailing_address_first_line, 
				city: res[i].mailing_address_city, 
				state: res[i].mailing_address_state, 
				postalCode: res[i].mailing_address_postal_code,
				country: res[i].mailing_address_country_code,
				organization_id: orgCreated[i].organization_id})
			);
		var addressCreated = await Address.bulkCreate(addressRecords);
		
		var addressRecords1 = res.map((x, i) => ({
				use: "work",
				line1: res[i].practice_location_first_line, 
				city: res[i].practice_location_city, 
				state: res[i].practice_location_state, 
				postalCode: res[i].practice_location_postal_code,
				country: res[i].practice_location_country_code,
				organization_id: orgCreated[i].organization_id})
			);
		var addressCreated1 = await Address.bulkCreate(addressRecords1);
		
		var telecomRecords = res.map((x, i) => ({
				system: "phone",
				value: res[i].mailing_address_telephone,
				use: "work",
				rank: 1,
				organization_id: orgCreated[i].organization_id})
			);
		var telecomCreated = await Telecom.bulkCreate(telecomRecords);
		
		var telecomRecords1 = res.map((x, i) => ({
				system: "fax",
				use: "work",
				rank: 1,
				value: res[i].mailing_address_fax,
				organization_id: orgCreated[i].organization_id})
			);
		var telecomCreated1 = await Telecom.bulkCreate(telecomRecords1);
		
		var telecomRecords2 = res.map((x, i) => ({
				system: "phone",
				value: res[i].practice_location_telephone,
				use: "work",
				rank: 2,
				organization_id: orgCreated[i].organization_id})
			);
		var telecomCreated2 = await Telecom.bulkCreate(telecomRecords2);
		
		var telecomRecords3 = res.map((x, i) => ({
				system: "fax",
				value: res[i].practice_location_fax,
				use: "work",
				rank: 2,
				organization_id: orgCreated[i].organization_id})
			);
		var telecomCreated3 = await Telecom.bulkCreate(telecomRecords3);
		
		var nameRecords = res.map((x, i) => ({
				use: "official",
				family: res[i].authorized_official_last_name,
				given: res[i].authorized_official_first_name})
			);
		var nameCreated = await Name.bulkCreate(nameRecords);
		
		var contactRecords = res.map((x, i) => ({
				name_id: nameCreated[i].name_id,
				organization_id: orgCreated[i].organization_id})
			);
		var contactCreated = await Contact.bulkCreate(contactRecords);
		
		var telecomRecords4 = res.map((x, i) => ({
				system: "phone",
				value: res[i].authorized_official_telephone,
				contact_id: contactCreated[i].contact_id,
				organization_id: orgCreated[i].organization_id})
			);
		var telecomCreated4 = await Telecom.bulkCreate(telecomRecords4);
		//console.log("bulk end: "+ Date.now());
	}

	},
	async getPractionerRoles() {
		for (var iteration = 0; iteration < 440; iteration++) {
			console.log("role iteration "+ iteration + " start at "+ Date.now());
			var limit = iteration * 10000;
			console.log("limit "+ limit);
		var providerQuery = "SELECT NPI, `Provider Last Name (Legal Name)` as last_name, "+
		"`Provider First Name` as first_name, "+
		"`Provider Middle Name` as middle_name, "+
		"`Provider First Line Business Practice Location Address` as practice_location_first_line,"+
		"`Provider Second Line Business Practice Location Address` as practice_location_second_line,"+
		"`Provider Business Practice Location Address City Name` as practice_location_city,"+
		"`Provider Business Practice Location Address State Name` as practice_location_state,"+
		"`Provider Business Practice Location Address Postal Code` as practice_location_postal_code,"+
		"`Provider Business Practice Location Address Ctry Code` as practice_location_country_code,"+
		"`Provider Business Practice Location Address Telephone Number` as practice_location_telephone,"+
		"`Provider Business Practice Location Address Fax Number` as practice_location_fax,"+
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
		"`Healthcare Provider Taxonomy Code_15` as healthcare_taxonomy_code_15 "+
		"FROM nppes.npi WHERE `Entity Type Code` = 1 "+
		"order by `Provider Last Name (Legal Name)`, `Provider First Name`, `Provider Middle Name` "+
		"limit "+ limit +", 10000";

		var res = await npidb.query(providerQuery, { model: Npi } );
		//console.log("NPI result: "+JSON.stringify(res));
		for (var i = 0; i < res.length; i++) {
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
				console.log("Org found: "+orgAddress.organization_id);
				var fullName = res[i].last_name+res[i].first_name+res[i].middle_name;
				var provider = await Provider.findOne({where: {photo: fullName}});
				if (provider != null){
					console.log("Role org found: "+orgAddress.organization_id);
					var roleCreated = await ProviderRole.create({
						active: '1',
						practitioner_id: provider.practitioner_id,
						organization_id: orgAddress.organization_id						
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
			}
		}
	}	

	},
	async getBulkProviders() {
	for (var iteration = 0; iteration < 440; iteration++) {
			console.log("provider iteration "+ iteration + " start at "+ Date.now());
			var limit = iteration * 10000;
			console.log("limit "+ limit);
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
		"order by `Provider Last Name (Legal Name)`, `Provider First Name`, `Provider Middle Name` limit "+
		limit +", 10000";
		//console.log("bulk start: "+ Date.now());
		var res = await npidb.query(providerQuery, { model: Npi } );
		var providerRecords = res.map((x, i) => {
			const obj = {};
			obj['active'] = '1';
			obj['gender'] = res[i].gender;
			obj['photo'] = res[i].last_name+res[i].first_name+res[i].middle_name;
			return obj;
			});
		var provCreated = await Provider.bulkCreate(providerRecords);
		//console.log("provider: "+ Date.now());
		var addressRecords1 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'billing';
			obj['line1'] = res[i].mailing_address_first_line;
			obj['city'] = res[i].mailing_address_city;
			obj['state'] = res[i].mailing_address_state;
			obj['postalCode'] = res[i].mailing_address_postal_code;
			obj['country'] = res[i].mailing_address_country_code;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var addressCreated1 = await Address.bulkCreate(addressRecords1);
		//console.log("address 1: "+ Date.now());
		var addressRecords2 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'work';
			obj['line1'] = res[i].practice_location_first_line;
			obj['city'] = res[i].practice_location_city;
			obj['state'] = res[i].practice_location_state;
			obj['postalCode'] = res[i].practice_location_postal_code;
			obj['country'] = res[i].practice_location_country_code;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var addressCreated12 = await Address.bulkCreate(addressRecords2);
		//console.log("address 2: "+ Date.now());
		var telecomRecords1 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'work';
			obj['value'] = res[i].mailing_address_telephone;
			obj['system'] = 'phone';
			obj['rank'] = 1;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var telecomCreated1 = await Telecom.bulkCreate(telecomRecords1);
		//console.log("telecom 1: "+ Date.now());
		var telecomRecords2 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'work';
			obj['value'] = res[i].mailing_address_fax;
			obj['system'] = 'fax';
			obj['rank'] = 1;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var telecomCreated2 = await Telecom.bulkCreate(telecomRecords2);
		//console.log("telecom 2: "+ Date.now());
		var telecomRecords3 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'work';
			obj['value'] = res[i].practice_location_telephone;
			obj['system'] = 'phone';
			obj['rank'] = 2;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var telecomCreated3 = await Telecom.bulkCreate(telecomRecords3);
		//console.log("telecom 3: "+ Date.now());
		var telecomRecords4 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'work';
			obj['value'] = res[i].practice_location_fax;
			obj['system'] = 'fax';
			obj['rank'] = 2;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var telecomCreated4 = await Telecom.bulkCreate(telecomRecords4);
		//console.log("telecom 4: "+ Date.now());
		var nameRecords1 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'official';
			obj['family'] = res[i].last_name;
			obj['given'] = res[i].first_name;
			obj['prefix'] = res[i].prefix;
			obj['suffix'] = res[i].suffix;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var nameCreated1 = await Name.bulkCreate(nameRecords1);
		//console.log("name 1: "+ Date.now());
		var nameRecords2 = res.map((x, i) => {
			const obj = {};
			obj['use'] = 'usual';
			obj['family'] = res[i].other_last_name;
			obj['given'] = res[i].other_first_name;
			obj['prefix'] = res[i].other_prefix;
			obj['suffix'] = res[i].other_suffix;
			obj['practitioner_id'] = provCreated[i].practitioner_id;
			return obj;
			});
		var nameCreated2 = await Name.bulkCreate(nameRecords2);
		//console.log("name 1: "+ Date.now());
	}

	}
};