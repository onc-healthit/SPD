package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.Address;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="Address")
public class VhDirAddress extends Address {
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
