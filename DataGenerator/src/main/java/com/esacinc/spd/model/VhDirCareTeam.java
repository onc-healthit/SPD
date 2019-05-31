package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**

*/

@ResourceDef(name="CareTeam", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-careteam")
public class VhDirCareTeam extends CareTeam { 
	private static final long serialVersionUID = 1L;
    

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;

	/**
	 * Add the careteam alias
	 */
	@Child(name="careteamAlias", type = {StringType.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/careteam-alias", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Alternate name for care team")
	private List<StringType> careteamAlias;


	/**
	 * Add the location reference
	 */
	@Child(name="locationReference", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/location-reference", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Where the care team operates")
	private List<Reference>locationReference;

	/**
	 * Add the healthcare service reference
	 */
	@Child(name="healthcareServiceReference", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/healthcareservice-reference", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Services provided by the care team")
	private List<Reference>healthcareServiceReference;

	/**
	 * Add the endpoint reference
	 */
	@Child(name="endpointReference", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/endpoint-reference", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Endpoints for the care team")
	private List<Reference>endpointReference;

	public List<Reference> getUsageRestriction() {
		return usageRestriction;
	}

	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirCareTeam addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}

	public List<StringType> getCareteamAlias() {
		return this.careteamAlias;
	}

	public void setCareteamAlias(List<StringType> val) {
		this.careteamAlias = val;
	}
	
	public VhDirCareTeam addCareteamAlias(StringType t) {
	    if (t == null)
	      return this;
	    if (this.careteamAlias == null)
	      this.careteamAlias = new ArrayList<StringType>();
	    this.careteamAlias.add(t);
	    return this;
	}

	public List<Reference> getLocationReference() {
		return locationReference;
	}

	public void setLocationReference(List<Reference> val) {
		this.locationReference = val;
	}
	
	public VhDirCareTeam addLocationReference(Reference t) {
	    if (t == null)
	      return this;
	    if (this.locationReference == null)
	      this.locationReference = new ArrayList<Reference>();
	    this.locationReference.add(t);
	    return this;
	}

	public List<Reference> getHealthcareServiceReference() {
		return healthcareServiceReference;
	}

	public void setHealthcareServiceReference(List<Reference> val) {
		this.healthcareServiceReference = val;
	}
	
	public VhDirCareTeam addHealthcareServiceReference(Reference t) {
	    if (t == null)
	      return this;
	    if (this.healthcareServiceReference == null)
	      this.healthcareServiceReference = new ArrayList<Reference>();
	    this.healthcareServiceReference.add(t);
	    return this;
	}

	public List<Reference> getEndpointReference() {
		return endpointReference;
	}

	public void setEndpointReference(List<Reference> val) {
		this.endpointReference = val;
	}
	
	// Since base careteam keeps a list of managing organizations, and VhDir expects only a single one,
	// create getter and setter to mimic a singleton.
	
	public void setManagingOrganization(Reference t) {
	    if (t == null) {
	    	this.managingOrganization = null;
	    	return ;
	    }
        this.managingOrganization = new ArrayList<Reference>();  // insure we always only have one element
		this.managingOrganization.add(t);
	}

	public VhDirCareTeam addEndpointReference(Reference t) {
	    if (t == null)
	      return this;
	    if (this.endpointReference == null)
	      this.endpointReference = new ArrayList<Reference>();
	    this.endpointReference.add(t);
	    return this;
	}

}
