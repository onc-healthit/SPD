package com.esacinc.spd.model;

import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.VerificationResult;

/**
 * The VhDir Validation profile does not add extensions or remove elements. It only modifies the 
 * cardinality of the following existing elements:
 * 
 * 	target to 1..1
 * 	need to 1..1
 * 	statusDate to 1..1
 * 	validationType to 1..1
 * 	validationProcess to 1..*
 * 	failureAction to 1..1
 * 
 * Therefore, we do not include those extension definitions as yet, as we don't model cardinality so far.
 * 
 * It also uses a PrimarySource profile with extensions.
 * 
 * @author dandonahue
 *
 */
@ResourceDef(name="vhdir-validation", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-validation")
public class VhDirValidation extends VerificationResult { 
	private static final long serialVersionUID = 1L;
    


	/**
     * A list of targets. The only modification from the base is that at least one is now required
     */
   // @Child(name = "target", type = {Reference.class},  min=1, max=Child.MAX_UNLIMITED, modifier=true, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/reference", definedLocally=false, isModifier=true)
    //@Description(shortDefinition="A resource that was validated", formalDefinition="A resource that was validated" )
    //protected List<Reference> target;

	/**
     * A codeable concept. The only modification from the base is that this is now required
     */
    //@Child(name = "need", type = {CodeableConcept.class},  min=1, max=1, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/CodeableConcept", definedLocally=false, isModifier=false)
    //@ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/verificationresult-need")
    //@Description(shortDefinition="Need: none | initial | periodic", formalDefinition="Need: none | initial | periodic" )
    //protected CodeableConcept need;

	/**
     * A statusDate, datetime. The only modification from the base is that this is now required 
     */
    //@Child(name = "statusDate", type = {DateTimeType.class},  min=1, max=1, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/statusDate", definedLocally=false, isModifier=false)
    //@Description(shortDefinition="When the validation status was updated", formalDefinition="When the validation status was updated" )
    //protected CodeableConcept statusDate;
   
	/**
     * A codeable concept. The only modification from the base is that this is now required
     */
    //@Child(name = "validationType", type = {CodeableConcept.class},  min=1, max=1, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/CodeableConcept", definedLocally=false, isModifier=false)
    //@ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/verificationresult-validation-type")
    //@Description(shortDefinition="Validation type: nothing|single|multiple", formalDefinition="Validation type: nothing|single|multiple" )
    //protected CodeableConcept validationType;
   
	/**
     * A list of codeable concepts. The only modification from the base is that  at least one required
     */
	//@Child(name = "validationProcess", type = {CodeableConcept.class},  min=1, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/validationProcess", definedLocally=false, isModifier=false)
	//@ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-validation")
	//@Description(shortDefinition="The process(es) by which the target is validated", formalDefinition="The process(es) by which the target is validated" )
	//protected List<CodeableConcept> validationProcess;

	/**
     * A codeable concept. The only modification from the base is that this is now required
     */
	//@Child(name = "failureAction", type = {CodeableConcept.class},  min=1, max=1, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/CodeableConcept", definedLocally=false, isModifier=false)
	// @Description(shortDefinition="Failure action", formalDefinition="The result if validation fails (fatal; warning; record only; none)." )
	//protected CodeableConcept failureAction;
   
}
