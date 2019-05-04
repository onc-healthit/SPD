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

@DatatypeDef(name="VhDirNewPatients")
public class VhDirNewpatients extends Type  implements ICompositeType{
	private static final long serialVersionUID = 1L;

	/**
     * A boolean 
     */
    @Child(name = "value", type = {BooleanType.class}, order=1, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Accepting patients", formalDefinition="Accepting patients" )
    protected BooleanType value;
    
	/**
     * A reference to a VhDirNetwork 
     */
    @Child(name = "reference", type = {Reference.class}, order=1, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Network", formalDefinition="Network" )
    protected Reference reference;
   
	/**
     * A URL to the definition
     */
    @Child(name = "url", type = {StringType.class}, order=1, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Definition url", formalDefinition="Definition url" )
    protected StringType url;
	
	
    public BooleanType getValue() {
    	return this.value;
    }
    
    public void setValue(BooleanType val) {
    	this.value = val;
    }
    
    public Reference getReference() {
    	return this.reference;
    }
    
    public void setReference(Reference val) {
    	this.reference = val;
    }
    
    public StringType getUrl() {
    	return this.url;
    }
    
    public void setUrl(StringType val) {
    	this.url = val;
    }

    public VhDirNewpatients() {
    	
    }
    
	@Override
	public VhDirNewpatients copy() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}


}
