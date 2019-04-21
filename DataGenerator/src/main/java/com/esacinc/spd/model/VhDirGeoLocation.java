package com.esacinc.spd.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.Extension;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="Geolocation")
public class VhDirGeoLocation extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;
    
	public VhDirGeoLocation setLatitude(BigDecimal value) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("latitude");
		ext.setValue(new DecimalType(value));
		this.extension.add(ext);
		return this;
	}

    public VhDirGeoLocation setLatitude(long value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("latitude");
		ext.setValue(new DecimalType(value));
		this.extension.add(ext);
		return this;
    }

    public VhDirGeoLocation setLatitude(double value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("latitude");
		ext.setValue(new DecimalType(value));
		this.extension.add(ext);
		return this;
    }
      
    public VhDirGeoLocation setLongitude(BigDecimal value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("longitude");
		ext.setValue(new DecimalType(value));
		this.extension.add(ext);
		return this;
    }

    public VhDirGeoLocation setLongitude(long value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("longitude");
		ext.setValue(new DecimalType(value));
		this.extension.add(ext);
		return this;
    }

    public VhDirGeoLocation setLongitude(double value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("longitude");
		ext.setValue(new DecimalType(value));
		this.extension.add(ext);
		return this;
    }
    
	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(extension);
    }

	@Override
	public VhDirGeoLocation copy() {
		VhDirGeoLocation retVal = new VhDirGeoLocation();
        super.copyValues(retVal);
        retVal.extension = extension;
        return retVal;
	}

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
