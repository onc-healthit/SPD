package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Type;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="contactpoint-availabletime")
public class VhDirContactPointAvailableTime extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the daysOfWeek
	 */
	@Child(name="daysOfWeek", type = {CodeType.class}, order=1, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="indicates the days of week for availability")
	private List<CodeType> daysOfWeek;
	
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

	public List<CodeType> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<CodeType> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}
	
	public VhDirContactPointAvailableTime addDaysOfWeek(CodeType t) {
	    if (t == null)
	      return this;
	    if (this.daysOfWeek == null)
	      this.daysOfWeek = new ArrayList<CodeType>();
	    this.daysOfWeek.add(t);
	    return this;
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

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
