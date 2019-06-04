package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import com.esacinc.spd.model.complex_extensions.IDigitalCertificate;
import com.esacinc.spd.model.complex_extensions.IEndpointUseCase;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;

@ResourceDef(name="Endpoint", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-endpoint")
public class VhDirEndpoint extends Endpoint implements IDigitalCertificate, IEndpointUseCase {
	private static final long serialVersionUID = 1L;

	/**
	 * Add the usage restriction
	 */
	@Child(name="usageRestriction", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/usage-restriction", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Restriction")
	private List<Reference>usageRestriction;

	/**
	 * Add the digital certificate
	 */
	@Child(name="digitalcertificate", type = {VhDirDigitalCertificate.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/digitalcertificate", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Certificate for the organization")
	private List<VhDirDigitalCertificate> digitalcertficate;

	/**
	 * Add the rank
	 */
	@Child(name="rank", type = {IntegerType.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/endpoint-rank", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Preferred order for connecting to the endpoint")
	private IntegerType rank;
	
	/**
	 * Add the use case
	 */
	@Child(name="endpointUsecase", type = {VhDirEndpointUseCase.class}, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/endpoint-usecase", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Use cases (service descriptions) supported by the endpoint")
	private List<VhDirEndpointUseCase>endpointUsecase;

	
	public List<Reference> getUsageRestriction() {
		return usageRestriction;
	}

	public void setUsageRestriction(List<Reference> val) {
		this.usageRestriction = val;
	}
	
	public VhDirEndpoint addUsageRestriction(Reference t) {
	    if (t == null)
	      return this;
	    if (this.usageRestriction == null)
	      this.usageRestriction = new ArrayList<Reference>();
	    this.usageRestriction.add(t);
	    return this;
	}

	public List<VhDirDigitalCertificate> getDigitalcertficate() {
		return digitalcertficate;
	}

	public void setDigitalcertficate(List<VhDirDigitalCertificate> digitalcertficate) {
		this.digitalcertficate = digitalcertficate;
	}
	
	public VhDirEndpoint addDigitalcertficate(VhDirDigitalCertificate t) {
	    if (t == null)
	      return this;
	    if (this.digitalcertficate == null)
	      this.digitalcertficate = new ArrayList<VhDirDigitalCertificate>();
	    this.digitalcertficate.add(t);
	    return this;
	}

	public List<VhDirEndpointUseCase> getEndpointUsecase() {
		return endpointUsecase;
	}

	public void setEndpointUsecase(List<VhDirEndpointUseCase> val) {
		this.endpointUsecase = val;
	}
	
	public VhDirEndpoint addEndpointUsecase(VhDirEndpointUseCase t) {
	    if (t == null)
	      return this;
	    if (this.endpointUsecase == null)
	      this.endpointUsecase = new ArrayList<VhDirEndpointUseCase>();
	    this.endpointUsecase.add(t);
	    return this;
	}

	public IntegerType getRank() {
		return rank;
	}

	public void setRank(IntegerType val) {
		this.rank = val;
	}

}
