package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;


import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**

*/

@ResourceDef(name="vhdir-practitionerrole", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-practitionerrole")
public class VhDirPractitionerRole extends PractitionerRole { 
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
	
	
	/**
     * A list of new patient indicators
     */
    @Child(name = "newpatients", type = {Element.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatients", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Whether the location is accepting new patients", formalDefinition="Whether the location is accepting new patients" )
    protected List<VhDirNewpatients> newpatients;
    
    /**
     * A list of new patient profile strings
     */
    @Child(name = "newpatientprofile", type = {Element.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatientprofile", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Type of new patients accepted", formalDefinition="Type of new patients accepted" )
    protected List<VhDirNewpatientprofile> newpatientprofile;
    
    /**
	 * Add the network reference
	 */
	@Child(name="networkReference", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/network-reference", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Where the care team operates")
	private List<Reference>networkReference;
	
	/**
	 * Add the qualification
	 */
	@Child(name="qualification", type = {VhDirQualification.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/qualification", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Qualification for the organization")
	private List<VhDirQualification> qualification;

    public List<Reference> getUsageRestriction() {
		return usageRestriction;
	}
	
    public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirPractitionerRole addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}


	public List<VhDirNewpatients> getNewpatients() {
		return newpatients;
	}

	public void setNewpatients(List<VhDirNewpatients> val) {
		this.newpatients = val;
	}
	
	public VhDirPractitionerRole addNewpatients(VhDirNewpatients t) {
	    if (t == null)
	      return this;
	    if (this.newpatients == null)
	      this.newpatients = new ArrayList<VhDirNewpatients>();
	    this.newpatients.add(t);
	    return this;
	}
	
	public List<VhDirNewpatientprofile> getNewpatientprofile() {
		return newpatientprofile;
	}

	public void setNewpatientprofile(List<VhDirNewpatientprofile> val) {
		this.newpatientprofile = val;
	}
	
	public VhDirPractitionerRole addNewpatientprofile(VhDirNewpatientprofile t) {
	    if (t == null)
	      return this;
	    if (this.newpatientprofile == null)
	      this.newpatientprofile = new ArrayList<VhDirNewpatientprofile>();
	    this.newpatientprofile.add(t);
	    return this;
	}
	
	public List<VhDirDigitalCertificate> getDigitalcertficate() {
		return digitalcertficate;
	}

	public void setDigitalcertficate(List<VhDirDigitalCertificate> digitalcertficate) {
		this.digitalcertficate = digitalcertficate;
	}
	
	public VhDirPractitionerRole addDigitalcertficate(VhDirDigitalCertificate t) {
	    if (t == null)
	      return this;
	    if (this.digitalcertficate == null)
	      this.digitalcertficate = new ArrayList<VhDirDigitalCertificate>();
	    this.digitalcertficate.add(t);
	    return this;
	}
	
	public List<Reference> getNetworkReference() {
		return networkReference;
	}

	public void setNetworkReference(List<Reference> val) {
		this.networkReference = val;
	}
	
	public VhDirPractitionerRole addNetworkReference(Reference t) {
	    if (t == null)
	      return this;
	    if (this.networkReference == null)
	      this.networkReference = new ArrayList<Reference>();
	    this.networkReference.add(t);
	    return this;
	}
	
	
	public List<VhDirQualification> getQualification() {
		return qualification;
	}

	public void setQualification(List<VhDirQualification> qualification) {
		this.qualification = qualification;
	}
	
	public VhDirPractitionerRole addQualification(VhDirQualification t) {
	    if (t == null)
	      return this;
	    if (this.qualification == null)
	      this.qualification = new ArrayList<VhDirQualification>();
	    this.qualification.add(t);
	    return this;
	}


}
