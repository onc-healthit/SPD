package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;

@ResourceDef(name="vhdir-organization", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-organization")
public class VhDirOrganization extends Organization {
	private static final long serialVersionUID = 1L;
    
	

	/**
	 * Add the organization description extension
	 */
	@Child(name="description", type = {StringType.class},  min=0, max=1, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/org-description", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Description of the organization")
	private StringType description;

	/**
	 * Add the digital certificate
	 */
	@Child(name="digitalcertificate", type = {VhDirDigitalCertificate.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/digitalcertificate", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Certificate for the organization")
	private List<VhDirDigitalCertificate> digitalcertficate;

	
	/**
	 * Add the qualification
	 */
	@Child(name="qualification", type = {VhDirQualification.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/qualification", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Qualification for the organization")
	private List<VhDirQualification> qualification;
	
     
    /**
     * Add the overridden alias
     */
    @Child(name= "orgAlias", type = {VhDirAlias.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/alias", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Alias for the organization")
	private List<VhDirAlias> orgAlias;
    

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;

	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(identifier, description, name, type, active,
        		alias, telecom, address);
    }
    
    // Getter for description
    public String getDescription() {
    	return this.description == null ? null : this.description.getValue();
    }
    
    // Setter for description
    public VhDirOrganization setDescription(String value) { 
        if (Utilities.noString(value))
          this.description = null;
        else {
          if (this.description == null)
            this.description = new StringType();
          this.description.setValue(value);
        }
        return this;
    }
 
 
	public List<VhDirDigitalCertificate> getDigitalcertficate() {
		return digitalcertficate;
	}

	public void setDigitalcertficate(List<VhDirDigitalCertificate> digitalcertficate) {
		this.digitalcertficate = digitalcertficate;
	}
	
	public VhDirOrganization addDigitalcertficate(VhDirDigitalCertificate t) {
	    if (t == null)
	      return this;
	    if (this.digitalcertficate == null)
	      this.digitalcertficate = new ArrayList<VhDirDigitalCertificate>();
	    this.digitalcertficate.add(t);
	    return this;
	}

	public List<VhDirQualification> getQualification() {
		return qualification;
	}

	public void setQualification(List<VhDirQualification> qualification) {
		this.qualification = qualification;
	}
	
	public VhDirOrganization addQualification(VhDirQualification t) {
	    if (t == null)
	      return this;
	    if (this.qualification == null)
	      this.qualification = new ArrayList<VhDirQualification>();
	    this.qualification.add(t);
	    return this;
	}


	
	   
    public void setOrgAlias(List<VhDirAlias> theAlias) { 
      this.orgAlias = theAlias;
     }

    public boolean hasOrgAlias() { 
      if (this.orgAlias == null)
        return false;
      for (VhDirAlias item : this.orgAlias)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /*
     public VhDirAlias_lite add_aliasElement() {//2 
    	VhDirAlias_lite t = new VhDirAlias_lite();
      if (this._alias == null)
        this._alias = new ArrayList<VhDirAlias_lite>();
      this._alias.add(t);
      return t;
    }
*/
     public VhDirOrganization addOrgAlias(VhDirAlias value) { //1
      if (this.orgAlias == null)
        this.orgAlias = new ArrayList<VhDirAlias>();
      this.orgAlias.add(value);
      return this;
    }
   
    
	public List<Reference> getUsageRestriction() {
		return usageRestriction;
	}

	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirOrganization addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}

	/*
     public OrganizationContactComponent getContactFirstRep() { 
      if (getContact().isEmpty()) {
        addContact();
      }
      return getContact().get(0);
    }private StringType description;
	
*/	
    


}
