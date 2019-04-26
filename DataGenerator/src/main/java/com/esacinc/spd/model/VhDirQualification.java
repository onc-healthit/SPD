package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="Qualification")
public class VhDirQualification extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;

	public VhDirQualification setIdentifer(Identifier id) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("identifier");
		ext.setValue(id);
		this.extension.add(ext);
		return this;
	}
	
	public VhDirQualification setCode(CodeableConcept code) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("code");
		ext.setValue(code);
		this.extension.add(ext);
		return this;
	}
	
	public VhDirQualification setIssuer(Reference issuer) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("issuer");
		ext.setValue(issuer);
		this.extension.add(ext);
		return this;
	}
	
	public VhDirQualification setStatus(Coding status) {
		if (this.extension == null)
			this.extension = new ArrayList<Extension>();
		Extension ext = new Extension();
		ext.setUrl("status");
		ext.setValue(status);
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
	public VhDirQualification copy() {
		VhDirQualification retVal = new VhDirQualification();
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
