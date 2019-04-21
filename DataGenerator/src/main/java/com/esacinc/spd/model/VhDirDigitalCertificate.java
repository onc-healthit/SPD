package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.Date;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="DigitalCertificate")
public class VhDirDigitalCertificate extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;
    
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

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
