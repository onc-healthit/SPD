package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.api.ICompositeType;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="VhDirNewPatientProfile")
public class VhDirNewpatientprofile extends Type  implements ICompositeType{
	private static final long serialVersionUID = 1L;

	/**
     * A string indicating the profile
     */
    @Child(name = "value", type = {StringType.class}, order=1, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="New patient profile", formalDefinition="New patient profile" )
    protected StringType value;
    
   
	/**
     * A URL to the definition
     */
    @Child(name = "url", type = {StringType.class}, order=1, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Definition url", formalDefinition="Definition url" )
    protected StringType url;
	
	
    public StringType getValue() {
    	return this.value;
    }
    
    public void setValue(StringType val) {
    	this.value = val;
    }
    
     
    public StringType getUrl() {
    	return this.url;
    }
    
    public void setUrl(StringType val) {
    	this.url = val;
    }

    public VhDirNewpatientprofile() {
    	
    }
    
	@Override
	public VhDirNewpatientprofile copy() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}


}
