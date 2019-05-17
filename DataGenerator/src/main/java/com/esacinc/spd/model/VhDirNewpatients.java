package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.api.ICompositeType;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
//import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="VhDirNewPatients")
public class VhDirNewpatients extends Extension  {
	private static final long serialVersionUID = 1L;

	/**
     * A boolean 
     */
    @Child(name = "acceptingPatients", type = {BooleanType.class}, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Accepting patients", formalDefinition="Accepting patients" )
    protected BooleanType acceptingPatients;
    
	/**
     * A reference to a VhDirNetwork 
     */
    @Child(name = "network", type = {Reference.class}, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Network", formalDefinition="Network" )
    protected Reference network;
   
	
	
    public BooleanType getAcceptingPatients() {
    	return this.acceptingPatients;
    }
    
    public void setAcceptingPatients(BooleanType val) {
    	this.acceptingPatients = val;
    }
    
    public Reference getNetwork() {
    	return this.network;
    }
    
    public void setNetwork(Reference val) {
    	this.network = val;
    }
 
    public VhDirNewpatients() {
    	
    }
    
	@Override
	public VhDirNewpatients copy() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}


}
