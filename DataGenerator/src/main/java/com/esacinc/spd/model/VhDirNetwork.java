package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**

*/

@ResourceDef(name="Network", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-network")
public class VhDirNetwork extends Organization { 
	private static final long serialVersionUID = 1L;
    

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;

	/**
	 * Add location reference
	 */
	@Child(name="location-reference", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/Reference", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Network Coverage area")
	private List<Reference> locationReference;


	/**
	 * Add organization reference
	 */
	@Child(name="organization-period", type = {Period.class}, min=0, max=1, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/Period", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Valid time period for this Network")
	private Period organizationPeriod;


	
	public List<Reference> getUsageRestriction() {
		return usageRestriction;
	}

	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirNetwork addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}

	public List<Reference> getLocationReference() {
		return locationReference;
	}

	public void setLocationReference(List<Reference> references) {
		this.locationReference = references;
		
	}
	
	public VhDirNetwork addLocationReference(Reference t) {
	    if (t == null)
	      return this;
	    if (this.locationReference == null)
	      this.locationReference = new ArrayList<Reference>();
	    this.locationReference.add(t);
	    return this;
	}

	public Period getOrganizationPeriod() {
		return organizationPeriod;
	}

	public void setOrganizationPeriod(Period val) {
		this.organizationPeriod = val;
		
	}
	
}
