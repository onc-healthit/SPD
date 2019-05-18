package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Reference;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;

import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="VhDirContactPoint")
public class VhDirTelecom extends ContactPoint {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the contactpoint-viaintermediary extension
	 */
	@Child(name="viaintermediary", type = {Reference.class})
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/contactpoint-viaintermediary", definedLocally=false, isModifier=false)
    @Description(shortDefinition="intermediary for contact")
    protected Reference viaintermediary;
	protected VhDirOrganization viaintermediaryTarget;
	
	/**
	 * Add the contact point available time
	 */
    @Child(name = "availabletime", type = {VhDirAvailableTime.class}, order=2, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/contactpoint-availabletime", definedLocally=false, isModifier=false)
    @Description(shortDefinition="A contact detail for the organization", formalDefinition="A contact detail for the organization." )
    protected List<VhDirAvailableTime> availabletime;
	
	public Reference getViaintermediary() {
		return viaintermediary;
	}

	public void setViaintermediary(Reference viaintermediary) {
		this.viaintermediary = viaintermediary;
	}

	public List<VhDirAvailableTime> getAvailableTime() {
		return availabletime;
	}

	public void setAvailableTime(List<VhDirAvailableTime> availabletime) {
		this.availabletime = availabletime;
	}
	
	public VhDirTelecom addAvailableTime(VhDirAvailableTime t) {
	    if (t == null)
	      return this;
	    if (this.availabletime == null)
	      this.availabletime = new ArrayList<VhDirAvailableTime>();
	    this.availabletime.add(t);
	    return this;
	}

	public boolean hasAvailableTime() {
		return (this.availabletime != null && !this.availabletime.isEmpty());
	}
	
	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(viaintermediary, availabletime);
    }
    
}
