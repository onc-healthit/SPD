package com.esacinc.spd.model;

import java.util.ArrayList;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Type;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="contactpoint-availabletime")
public class VhDirAvailableTime extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;

	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(extension);
    }

	@Override
	public VhDirAvailableTime copy() {
		VhDirAvailableTime retVal = new VhDirAvailableTime();
        super.copyValues(retVal);
        retVal.extension = extension;
        return retVal;
	}
	
	public VhDirAvailableTime addDaysOfWeek(CodeType t) {
	    if (t == null)
	      return this;
	    if (this.extension == null)
	      this.extension = new ArrayList<Extension>();
	    Extension ext = new Extension();
	    ext.setUrl("daysOfWeek");
	    ext.setValue(t);
	    this.extension.add(ext);
	    return this;
	}

	public VhDirAvailableTime setAllDay(boolean allDay) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("allDay");
		ext.setValue(new BooleanType(allDay));
		this.extension.add(ext);
		return this;
	}

	public VhDirAvailableTime setAvailableStartTime(TimeType availableStartTime) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("availableStartTime");
		ext.setValue(availableStartTime);
		this.extension.add(ext);
		return this;
	}

	public VhDirAvailableTime setAvailableEndTime(TimeType availableEndTime) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("availableEndTime");
		ext.setValue(availableEndTime);
		this.extension.add(ext);
		return this;
	}

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
