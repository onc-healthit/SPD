package com.esacinc.spd.model;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.DateType;
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
    
	/**
	 * Add the identifier
	 */
	@Child(name="identifier", type = {Identifier.class}, order=1, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="Identifier for the qualification")
	private List<Identifier> identifier;
	
	/**
	 * Add the code
	 */
	@Child(name="code", type = {CodeableConcept.class}, order=2, min=0, max=1, modifier=false, summary=false)
    @Description(shortDefinition="indicates the type of qualification")
	private CodeableConcept code;
	
	/**
	 * Add the issuer
	 */
	@Child(name="issuer", type = {VhDirOrganization.class}, order=3, min=0, max=1, modifier=false, summary=false)
    @Description(shortDefinition="issuer of the qualification")
	private Reference issuer;
	
	/**
	 * Add the status
	 */
	@Child(name="status", type = {Coding.class}, order=4, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="status of the qualification")
	private List<Coding> status;
	
	/**
	 * Add the period
	 */
	@Child(name="period", type = {Period.class}, order=5, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="indicates when the qualification was valid")
	private List<Period> period;

	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(identifier, code, issuer,
        		status, period);
    }

	@Override
	public VhDirQualification copy() {
		VhDirQualification retVal = new VhDirQualification();
        super.copyValues(retVal);
        retVal.identifier = identifier;
        retVal.code = code;
        retVal.status = status;
        retVal.period = period;
        retVal.issuer = issuer;
        return retVal;
	}

	@Override
	protected Type typedCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
