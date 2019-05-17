package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;

@DatatypeDef(name="VhDirEhr")
public class VhDirEhr extends Extension {
	private static final long serialVersionUID = 1L;

	/**
     * Developer
     */
    @Child(name = "developer", type = {StringType.class},  min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Developer", formalDefinition="Developer" )
    protected StringType developer;

	/**
     * Product
     */
    @Child(name = "product", type = {StringType.class},  min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Product", formalDefinition="Product" )
    protected StringType product;
    
	/**
     * Version
     */
    @Child(name = "version", type = {StringType.class},  min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Version", formalDefinition="Version" )
    protected StringType version;
   
	/**
     * Certification Edition
     */
    @Child(name = "certificationEdition", type = {Coding.class},  min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Certification Edition", formalDefinition="Certification Edition" )
    protected Coding certificationEdition;

	/**
     * Certification ID
     */
    @Child(name = "certificationID", type = {StringType.class},  min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Certification ID", formalDefinition="Certification ID" )
    protected StringType certificationID;

    
	/**
     * Patient Access
     */
    @Child(name = "patientAccess", type = {CodeableConcept.class}, min=1, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Certification Edition", formalDefinition="Certification Edition" )
    protected List<CodeableConcept> patientAccess;


    public StringType getDeveloper() {
    	return this.developer;
    }
    
    public void setDeveloper(StringType val) {
    	this.developer = val;
    }

    public StringType getProduct() {
    	return this.product;
    }
    
    public void setProduct(StringType val) {
    	this.product = val;
    }
   
    public StringType getVersion() {
    	return this.version;
    }
    
    public void setVersion(StringType val) {
    	this.version = val;
    }

    public Coding getCertificationEdition() {
    	return this.certificationEdition;
    }
    
    public void setCertificationEdition(Coding val) {
    	this.certificationEdition = val;
    }

    public StringType getCertificationId() {
    	return this.certificationID;
    }
    
    public void setCertificationID(StringType val) {
    	this.certificationID = val;
    }

    public List<CodeableConcept> getPatientAccess() {
    	return this.patientAccess;
    }
    
    public void setPatientAccess(List<CodeableConcept> val) {
    	this.patientAccess = val;
    }

	public VhDirEhr addPatientAccess(CodeableConcept t) {
	    if (t == null)
	      return this;
	    if (this.patientAccess == null)
	      this.patientAccess = new ArrayList<CodeableConcept>();
	    this.patientAccess.add(t);
	    return this;
	}

  
	@Override
	public VhDirEhr copy() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected VhDirEhr typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}


}
