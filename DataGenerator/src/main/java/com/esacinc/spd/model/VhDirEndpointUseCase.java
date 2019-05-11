package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.UrlType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
//import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="UseCase")
public class VhDirEndpointUseCase extends Extension {
	private static final long serialVersionUID = 1L;

	/**
	 * Add the Type
	 */
	@Child(name = "caseType", type = {CodeableConcept.class})
	//@Extension(url="http://hl7.org/fhir/StructureDefinition/CodeableConcept", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Use case type")
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-usecase")
    protected CodeableConcept caseType;

	public CodeableConcept getCaseType() {
		return this.caseType;
	}

	public void setCaseType(CodeableConcept val) {
		this.caseType = val;
	}

	/**
	 * Add the standard
	 */
	@Child(name = "standard", type = {UriType.class})
	//@Extension(url="http://hl7.org/fhir/StructureDefinition/UriType", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Use case standard")
    protected UriType standard;

	public UriType getStandard() {
		return this.standard;
	}

	public void setStandard(UriType val) {
		this.standard = val;
	}



}
