package com.esacinc.spd.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import com.esacinc.spd.model.complex_extensions.IGeoLocation;
import org.hl7.fhir.r4.model.Address;

@DatatypeDef(name="vhdir-address")
public class VhDirAddress extends Address implements IGeoLocation {
	private static final long serialVersionUID = 1L;

	/**
	 * Add the geolocation
	 */
	@Child(name = "geolocation", type = {VhDirGeoLocation.class})
	@Extension(url="http://hl7.org/fhir/StructureDefinition/geolocation", definedLocally=false, isModifier=false)
    @Description(shortDefinition="geolocation of the organization")
    protected VhDirGeoLocation geolocation;

	public VhDirGeoLocation getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(VhDirGeoLocation geolocation) {
		this.geolocation = geolocation;
	}
}
