package com.esacinc.spd.model;

import java.util.Date;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.util.ElementUtil;

@DatatypeDef(name="DigitalCertificate")
public class VhDirDigitalCertificate extends Type implements ICompositeType {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the type of certificate
	 */
	@Child(name="type", type = {Coding.class})
    @Description(shortDefinition="indicates the type of digital certificate")
	private Coding type;
	
	/**
	 * Add the use for the certificate
	 */
	@Child(name="use", type = {Coding.class})
    @Description(shortDefinition="indicates the purpose of the digital certificate")
	private Coding use;
	
	/**
	 * Add the standard for the certificate
	 */
	@Child(name="certificateStandard", type = {Coding.class})
    @Description(shortDefinition="indicates the certificate standard")
	private Coding certificateStandard;
	
	/**
	 * Add the certificate
	 */
	@Child(name="certificate", type = {StringType.class})
    @Description(shortDefinition="a string representation of a PEM format certificate")
	private StringType certificate;
	
	/**
	 * Add the certificate expiration
	 */
	@Child(name="expirationDate", type = {DateType.class})
    @Description(shortDefinition="indicates when the certificate expires")
	private DateType expirationDate;
	
	/**
	 * Add the trust framework
	 */
	@Child(name="trustFramework", type = {CodeableConcept.class})
    @Description(shortDefinition="indicates any trust frameworks supported by the certificate")
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate")
	private CodeableConcept trustFramework;
	
	public Coding getType() {
		return type;
	}

	public void setType(Coding type) {
		this.type = type;
	}
	
	public Coding getUse() {
		return use;
	}

	public void setUse(Coding use) {
		this.use = use;
	}
	
	public Coding getCertificateStandard() {
		return certificateStandard;
	}

	public void setCertificateStandard(Coding std) {
		this.certificateStandard = std;
	}
	
    public String getCertificate() {
    	return this.certificate == null ? null : this.certificate.getValue();
    }
    
    public VhDirDigitalCertificate setCertificate(String value) { 
        if (Utilities.noString(value))
          this.certificate = null;
        else {
          if (this.certificate == null)
            this.certificate = new StringType();
          this.certificate.setValue(value);
        }
        return this;
    }
    
    public Date ExpirationDate() {
    	return this.expirationDate == null ? null : this.expirationDate.getValue();
    }
    
    public VhDirDigitalCertificate setExpirationDate(Date value) {
    	this.expirationDate = new DateType();
    	this.expirationDate.setValue(value);
    	return this;
    }
    
    public CodeableConcept getTrustFramework() { 
        if (this.trustFramework == null)
          if (Configuration.errorOnAutoCreate())
            throw new Error("Attempt to auto-create trustframework");
          else if (Configuration.doAutoCreate())
            this.trustFramework = new CodeableConcept();
        return this.trustFramework;
      }

      public boolean hasTrustFramework() { 
          return this.trustFramework != null && !this.trustFramework.isEmpty();
      }

      public VhDirDigitalCertificate setTrustFramework(CodeableConcept value) { 
        this.trustFramework = value;
        return this;
      }

	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(type, use, certificateStandard,
        		certificate, expirationDate, trustFramework);
    }

	@Override
	public VhDirDigitalCertificate copy() {
		VhDirDigitalCertificate retVal = new VhDirDigitalCertificate();
        super.copyValues(retVal);
        retVal.type = type;
        retVal.use = use;
        retVal.certificateStandard = certificateStandard;
        retVal.certificate = certificate;
        retVal.expirationDate = expirationDate;
        retVal.trustFramework = trustFramework;
        return retVal;
	}

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
