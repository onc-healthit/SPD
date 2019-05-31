package com.esacinc.spd.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import com.esacinc.spd.model.complex_extensions.IDigitalCertificate;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;

import java.util.ArrayList;
import java.util.List;

/**
   The VhDirPractioner profile extends the base FHIR Practitioner resource.
    It has two extensions:  
	   - usage-restriction  (a FHIR reference)
	   - digitalcertificate, modeled in VhDirDigitalCertificate (a FHIR complex type)
	   
	It also uses versions of other FHIR resources that contain extensions within them, and are therefore defined as separate
	classes that extend the original FHIR resource class:
	   - VhDirIdentifer extends identifier
	   - VhDirContactPoint extends ContactPoint
	   - VhDirAddres extends Address 
	   - VhDirQualiication extends Type
	   
	The fields that continue to use unextended standard FHIR resources are:
	   - name uses HumanName
	   - active uses boolean type
	   - gender uses AdministrativeGender code
	   - birthDate uses date type
	   - communication uses CodeableConcept

*/

@ResourceDef(name="Practitioner", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-practitioner")
public class VhDirPractitioner extends Practitioner implements IDigitalCertificate {
	private static final long serialVersionUID = 1L;
    

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;

	/**
	 * Add the digital certificate
	 */
	@Child(name="digitalcertificate", type = {VhDirDigitalCertificate.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/digitalcertificate", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Certificate for the organization")
	private List<VhDirDigitalCertificate> digitalcertficate;

	
	public List<Reference> getUsageRestriction() {
		return usageRestriction;
	}

	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirPractitioner addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}

	public List<VhDirDigitalCertificate> getDigitalcertficate() {
		return digitalcertficate;
	}

	public void setDigitalcertficate(List<VhDirDigitalCertificate> digitalcertficate) {
		this.digitalcertficate = digitalcertficate;
	}
	
	public VhDirPractitioner addDigitalcertficate(VhDirDigitalCertificate t) {
	    if (t == null)
	      return this;
	    if (this.digitalcertficate == null)
	      this.digitalcertficate = new ArrayList<VhDirDigitalCertificate>();
	    this.digitalcertficate.add(t);
	    return this;
	}

	
}
