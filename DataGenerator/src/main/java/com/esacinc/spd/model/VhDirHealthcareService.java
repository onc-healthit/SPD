package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**

*/

@ResourceDef(name="vhdir-healthcareservice", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-healthcareservice")
public class VhDirHealthcareService extends HealthcareService { 
	private static final long serialVersionUID = 1L;
    

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;


	/**
     * A list of new patient indicators
     */
    @Child(name = "newpatients", type = {Element.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatients", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Whether the location is accepting new patients", formalDefinition="Whether the location is accepting new patients" )
    protected List<VhDirNewpatients> newpatients;


	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirHealthcareService addUsageRestriction(Reference t) {
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
	
	public VhDirHealthcareService addNewpatients(VhDirNewpatients t) {
	    if (t == null)
	      return this;
	    if (this.newpatients == null)
	      this.newpatients = new ArrayList<VhDirNewpatients>();
	    this.newpatients.add(t);
	    return this;
	}

}
