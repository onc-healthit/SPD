package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.TimeType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;

@ResourceDef(name="ContactPointAvailableTime", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/contactpoint-availabletime")
public class VhDirContactPointAvailableTime extends DomainResource {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the daysOfWeek
	 */
	@Child(name="daysOfWeek", type = {CodeType.class})
    @Description(shortDefinition="indicates the days of week for availability")
	private CodeType daysOfWeek;
	
	/**
	 * Add the allDay
	 */
	@Child(name="allDay", type = {BooleanType.class})
    @Description(shortDefinition="indicates if they are available all day")
	private BooleanType allDay;
	
	/**
	 * Add the availableStartTime
	 */
	@Child(name="availableStartTime", type = {TimeType.class})
    @Description(shortDefinition="available start time")
	private TimeType availableStartTime;
	
	/**
	 * Add the availableEndTime
	 */
	@Child(name="availableEndTime", type = {TimeType.class})
    @Description(shortDefinition="available end time")
	private TimeType availableEndTime;

	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(daysOfWeek, allDay, availableStartTime,
        		availableEndTime);
    }

	@Override
	public VhDirContactPointAvailableTime copy() {
		VhDirContactPointAvailableTime retVal = new VhDirContactPointAvailableTime();
        super.copyValues(retVal);
        retVal.daysOfWeek = daysOfWeek;
        retVal.allDay = allDay;
        retVal.availableStartTime = availableStartTime;
        retVal.availableEndTime = availableEndTime;
        return retVal;
	}

	@Override
	public ResourceType getResourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	public CodeType getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(CodeType daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public boolean getAllDay() {
		return allDay.booleanValue();
	}

	public void setAllDay(boolean allDay) {
		this.allDay = new BooleanType();
		this.allDay.setValue(allDay);
	}

	public TimeType getAvailableStartTime() {
		return availableStartTime;
	}

	public void setAvailableStartTime(TimeType availableStartTime) {
		this.availableStartTime = availableStartTime;
	}

	public TimeType getAvailableEndTime() {
		return availableEndTime;
	}

	public void setAvailableEndTime(TimeType availableEndTime) {
		this.availableEndTime = availableEndTime;
	}
}
