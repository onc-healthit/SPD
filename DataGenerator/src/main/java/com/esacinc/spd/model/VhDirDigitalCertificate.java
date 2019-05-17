package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.Date;

import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.BackboneElement;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="VhDirDigitalCertificate")
public class VhDirDigitalCertificate extends Extension {
	private static final long serialVersionUID = 1L;

	/**
	 * Add the type
	 */
	@Child(name="type", type = {Coding.class}, order=1, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Type")
	private Coding type;

	/**
	 * Add the use
	 */
	@Child(name="use", type = {Coding.class}, order=2, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Use")
	private Coding use;

	/**
	 * Add the standard
	 */
	@Child(name="certificateStandard", type = {Coding.class}, order=3, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="certificateStandard")
	private Coding certificateStandard;

	/**
	 * Add the cert
	 */
	@Child(name="certificate", type = {StringType.class}, order=4, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Certificate")
	private StringType certificate;

	/**
	 * Add the expiration date
	 */
	@Child(name="expirationDate", type = {DateType.class}, order=5, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Expiration Date")
	private DateType expirationDate;

	/**
	 * Add the expiration date
	 */
	@Child(name="trustFramework", type = {CodeableConcept.class}, order=6, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="trustFramework Date")
	private CodeableConcept trustFramework;

	public Coding getType() {
		return this.type;
	}
	
/*
	public void setType(Coding val) {
		this.type = val;
	}
	
	public Coding getUse() {
		return this.use;
	}
	
	public void setUse(Coding val) {
		this.use = val;
	}

	public DateType getExpirationDate() {
		return this.expirationDate;
	}
	
	public void setExpirationDate(DateType val) {
		this.expirationDate = val;
	}

	public Coding getCertificateStandard() {
		return this.certificateStandard;
	}
	
	public void setCertificateStandard(Coding val) {
		this.certificateStandard = val;
	}

	public StringType getCertificate() {
		return this.certificate;
	}
	
	public void setCertificate(StringType val) {
		this.certificate = val;
	}

	public CodeableConcept getTrustFramework() {
		return this.trustFramework;
	}
	
	public void setTrustFramework(CodeableConcept val) {
		this.trustFramework = val;
	}
	*/
	
	public VhDirDigitalCertificate setType(Coding type) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("type");
		ext.setValue(type);
		this.extension.add(ext);
		return this;
	}

	public VhDirDigitalCertificate setUse(Coding use) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("use");
		ext.setValue(use);
		this.extension.add(ext);
		return this;
	}

	public VhDirDigitalCertificate setCertificateStandard(Coding std) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("certificateStandard");
		ext.setValue(std);
		this.extension.add(ext);
		return this;
	}
    
    public VhDirDigitalCertificate setCertificate(String value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("certificate");
		ext.setValue(new StringType(value));
		this.extension.add(ext);
		return this;
    }
    
    public VhDirDigitalCertificate setExpirationDate(Date value) {
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("expirationDate");
		ext.setValue(new DateType(value));
		this.extension.add(ext);
		return this;
    }
    
    public VhDirDigitalCertificate setTrustFramework(CodeableConcept value) { 
    	if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("trustFramework");
		ext.setValue(value);
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
	public VhDirDigitalCertificate copy() {
		VhDirDigitalCertificate retVal = new VhDirDigitalCertificate();
        super.copyValues(retVal);
        retVal.extension = extension;
        return retVal;
	}


}
