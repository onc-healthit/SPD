package com.esacinc.spd.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.util.ElementUtil;
import com.esacinc.spd.model.complex_extensions.IAvailableTime;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Reference;

import java.util.ArrayList;
import java.util.List;

@DatatypeDef(name="vhdir-telecom")
public class VhDirTelecom extends ContactPoint implements IAvailableTime {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the contactpoint-viaintermediary extension
	 */
	@Child(name="viaintermediary")
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/contactpoint-viaintermediary", definedLocally=false, isModifier=false)
    @Description(shortDefinition="intermediary for contact")
    protected Reference viaintermediary;
	protected VhDirOrganization viaintermediaryTarget;
	
	/**
	 * Add the contact point available time
	 */
    @Child(name = "availabletime", order=2, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/contactpoint-availabletime", definedLocally=true, isModifier=false)
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
