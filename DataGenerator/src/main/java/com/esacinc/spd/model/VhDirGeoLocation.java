package com.esacinc.spd.model;

import java.math.BigDecimal;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Type;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="Geolocation")
public class VhDirGeoLocation extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the latitude
	 */
	@Child(name="latitude", type = {DecimalType.class})
    @Description(shortDefinition="latitude")
	private DecimalType latitude;

	/**
	 * Add the longitude
	 */
	@Child(name="longitude", type = {DecimalType.class})
    @Description(shortDefinition="longitude")
	private DecimalType longitude;

    public BigDecimal getLatitude() { 
      return this.latitude == null ? null : this.latitude.getValue();
    }
    
    public VhDirGeoLocation setLatitude(BigDecimal value) { 
      if (value == null)
        this.latitude = null;
      else {
        if (this.latitude == null)
          this.latitude = new DecimalType();
        this.latitude.setValue(value);
      }
      return this;
    }

    public VhDirGeoLocation setLatitude(long value) { 
          this.latitude = new DecimalType();
        this.latitude.setValue(value);
      return this;
    }

    public VhDirGeoLocation setLatitude(double value) { 
          this.latitude = new DecimalType();
        this.latitude.setValue(value);
      return this;
    }
    
    public BigDecimal getLongitude() { 
        return this.longitude == null ? null : this.longitude.getValue();
      }
      
      public VhDirGeoLocation setLongitude(BigDecimal value) { 
        if (value == null)
          this.longitude = null;
        else {
          if (this.longitude == null)
            this.longitude = new DecimalType();
          this.longitude.setValue(value);
        }
        return this;
      }

      public VhDirGeoLocation setLongitude(long value) { 
            this.longitude = new DecimalType();
          this.longitude.setValue(value);
        return this;
      }

      public VhDirGeoLocation setLongitude(double value) { 
            this.longitude = new DecimalType();
          this.longitude.setValue(value);
        return this;
      }
    
	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(latitude, longitude);
    }

	@Override
	public VhDirGeoLocation copy() {
		VhDirGeoLocation retVal = new VhDirGeoLocation();
        super.copyValues(retVal);
        retVal.latitude = latitude;
        retVal.longitude = longitude;
        return retVal;
	}

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
