package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**

*/

@ResourceDef(name="vhdir-location", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-location")
public class VhDirLocation extends Location { 
	private static final long serialVersionUID = 1L;
    

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;


	/**
     * A list of codeable concepts for accessibility
     */
    @Child(name = "accessibility", type = {CodeableConcept.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/accessibility", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Accessibility options offered by the location", formalDefinition="Accessibility options offered by the location" )
    protected List<CodeableConcept> accessibility;

	/**
     * A list of ehr
     */
    @Child(name = "ehr", type = {Element.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/ehr", definedLocally=false, isModifier=false)
    @Description(shortDefinition="EHR at the location", formalDefinition="EHR at the location" )
    protected List<VhDirEhr> ehr;

	/**
     * A list of new patient indicators
     */
    @Child(name = "newpatients", type = {Element.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatients", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Whether the location is accepting new patients", formalDefinition="Whether the location is accepting new patients" )
    protected List<VhDirNewpatients> newpatients;

	/**
     * A list of new patient profile strings
     */
    @Child(name = "newpatientprofile", type = {Element.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatientprofile", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Type of new patients accepted", formalDefinition="Type of new patients accepted" )
    protected List<VhDirNewpatientprofile> newpatientprofile;

	/**
     * A list of geolocation attachments - currently not modeled
     */
    @Child(name = "location-boundary-geojson", type = {StringType.class},  min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/location-boundary-geojson", definedLocally=false, isModifier=false)
    @Description(shortDefinition="A boundary shape that represents the outside edge of the location (in GeoJSON format)", formalDefinition="A boundary shape that represents the outside edge of the location (in GeoJSON format)" )
    protected List<StringType> location_boundary_geojson;

    
    
	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirLocation addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}

	public List<CodeableConcept> getAccessibility() {
		return accessibility;
	}

	public void setAccessibility(List<CodeableConcept> val) {
		this.accessibility = val;
	}
	
	public VhDirLocation addAccessibility(CodeableConcept t) {
	    if (t == null)
	      return this;
	    if (this.accessibility == null)
	      this.accessibility = new ArrayList<CodeableConcept>();
	    this.accessibility.add(t);
	    return this;
	}

	public List<VhDirEhr> getEhr() {
		return ehr;
	}

	public void setEhr(List<VhDirEhr> val) {
		this.ehr = val;
	}
	
	public VhDirLocation addEhr(VhDirEhr t) {
	    if (t == null)
	      return this;
	    if (this.ehr == null)
	      this.ehr = new ArrayList<VhDirEhr>();
	    this.ehr.add(t);
	    return this;
	}

	public List<VhDirNewpatients> getNewpatients() {
		return newpatients;
	}

	public void setNewpatients(List<VhDirNewpatients> val) {
		this.newpatients = val;
	}
	
	public VhDirLocation addNewpatients(VhDirNewpatients t) {
	    if (t == null)
	      return this;
	    if (this.newpatients == null)
	      this.newpatients = new ArrayList<VhDirNewpatients>();
	    this.newpatients.add(t);
	    return this;
	}

	public List<VhDirNewpatientprofile> getNewpatientprofile() {
		return newpatientprofile;
	}

	public void setNewpatientprofile(List<VhDirNewpatientprofile> val) {
		this.newpatientprofile = val;
	}
	
	public VhDirLocation addNewpatientprofile(VhDirNewpatientprofile t) {
	    if (t == null)
	      return this;
	    if (this.newpatientprofile == null)
	      this.newpatientprofile = new ArrayList<VhDirNewpatientprofile>();
	    this.newpatientprofile.add(t);
	    return this;
	}

	public List<StringType> getLocation_boundary_geojson() {
		return location_boundary_geojson;
	}

	public void setLocation_boundary_geojson(List<StringType> val) {
		this.location_boundary_geojson = val;
	}
	
	public VhDirLocation addLocation_boundary_geojson(StringType t) {
	    if (t == null)
	      return this;
	    if (this.location_boundary_geojson == null)
	      this.location_boundary_geojson = new ArrayList<StringType>();
	    this.location_boundary_geojson.add(t);
	    return this;
	}

}
