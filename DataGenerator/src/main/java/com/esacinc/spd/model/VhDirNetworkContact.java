package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="OrganizationContactComponent")
public class VhDirNetworkContact extends OrganizationContactComponent {
	private static final long serialVersionUID = 1L;
    
	
	/**
	 * Add the contact purpose
	 */
    @Child(name = "purpose", type = {CodeableConcept.class},  min=0, max=1, modifier=false, summary=false)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/codeableconcept", definedLocally=false, isModifier=false)
    @Description(shortDefinition="The type of contact", formalDefinition="Binding: ContactEntityType (extensible)" )
    protected CodeableConcept purpose;
	
	public CodeableConcept getPurpose() {
		return purpose;
		}

	public OrganizationContactComponent setPurpose(CodeableConcept val) {
		this.purpose = val;
		return this;
	}
    
}
