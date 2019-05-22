package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;

@DatatypeDef(name="vhdir-qualification")
public class VhDirQualification extends Extension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Child(name = "identifier", type = {Identifier.class}, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="Identifiers")
    protected List<Identifier>identifier;

	@Child(name = "code", type = {CodeableConcept.class}, min=1, max=1)
    @Description(shortDefinition="Code")
    protected CodeableConcept code;

	@Child(name = "issuer", type = {Reference.class}, min=1, max=1)
    @Description(shortDefinition="Issuer")
    protected Reference issuer;

	@Child(name = "status", type = {Coding.class}, min=0, max=1)
    @Description(shortDefinition="Status")
    protected Coding status;
	
	@Child(name = "period", type = {Period.class}, min=0, max=1)
    @Description(shortDefinition="Period")
    protected Period period;

	@Child(name = "whereValid", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="whereValid")
    protected List<Reference> whereValid;
	
	
	public List<Identifier> getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(List<Identifier> val) {
		this.identifier = val;
	}
	
	public VhDirQualification addIdentifier(Identifier t) {
	    if (t == null)
	      return this;
	    if (this.identifier == null)
	      this.identifier = new ArrayList<Identifier>();
	    this.identifier.add(t);
	    return this;
	}

	public CodeableConcept getCode() {
		return this.code;
	}

	public void setCode(CodeableConcept val) {
		this.code = val;
	}
	
	public Reference getIssuer() {
		return this.issuer;
	}

	public void setIssuer(Reference val) {
		this.issuer = val;
	}

	public Coding getStatus() {
		return this.status;
	}

	public void setStatus(Coding val) {
		this.status = val;
	}

	public Period getPeriod() {
		return this.period;
	}

	public void setPeriod(Period val) {
		this.period = val;
	}

	public List<Reference> getWhereValid() {
		return this.whereValid;
	}

	public void setWhereValid(List<Reference> val) {
		this.whereValid = val;
	}
	
	public VhDirQualification addWhereValid(Reference t) {
	    if (t == null)
	      return this;
	    if (this.whereValid == null)
	      this.whereValid = new ArrayList<Reference>();
	    this.whereValid.add(t);
	    return this;
	}
	

}
