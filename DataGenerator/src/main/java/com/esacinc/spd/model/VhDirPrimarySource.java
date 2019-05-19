package com.esacinc.spd.model;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;

@DatatypeDef(name="vhdir-primarysource")
public class VhDirPrimarySource extends VerificationResultPrimarySourceComponent {
	private static final long serialVersionUID = 1L;

	/**
     * Type list. The only modification from the base is that at least one is now required
     */
    @Child(name = "type", type = {CodeableConcept.class}, min=1, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Type of primary source", formalDefinition="Type of primary source" )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/verificationresult-primary-source-type")
    protected List<CodeableConcept> type;
    

}
