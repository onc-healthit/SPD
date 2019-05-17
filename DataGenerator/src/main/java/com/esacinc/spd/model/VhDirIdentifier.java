package com.esacinc.spd.model;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.EnumFactory;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.PrimitiveType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="VhDirIdentifier")
public class VhDirIdentifier extends Identifier {
	private static final long serialVersionUID = 1L;
	
	public enum IdentifierStatus {
        ACTIVE,
        INACTIVE,
        ISSUEDINERROR,
        REVOKED,
        PENDING,
        UNKNOWN;
        
        public static IdentifierStatus fromCode(String codeString) throws FHIRException {
	        if (codeString == null || "".equals(codeString))
	            return null;
	        if ("active".equals(codeString))
	          return ACTIVE;
	        if ("inactive".equals(codeString))
	          return INACTIVE;
	        if ("issuedinerror".equals(codeString))
	          return ISSUEDINERROR;
	        if ("revoked".equals(codeString))
	          return REVOKED;
	        if ("pending".equals(codeString))
	          return PENDING;
	        if ("unknown".equals(codeString))
		      return UNKNOWN;
	        if (Configuration.isAcceptInvalidEnums())
	          return null;
	        else
	          throw new FHIRException("Unknown IdentifierUse code '"+codeString+"'");
	    }
        public String toCode() {
          switch (this) {
            case ACTIVE: return "active";
            case INACTIVE: return "inactive";
            case ISSUEDINERROR: return "issuedinerror";
            case REVOKED: return "revoked";
            case PENDING: return "pending";
            case UNKNOWN: return "unknown";
            default: return "?";
          }
        }
        public String getSystem() {
          switch (this) {
            case ACTIVE: return "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus";
            case INACTIVE: return "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus";
            case ISSUEDINERROR: return "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus";
            case REVOKED: return "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus";
            case PENDING: return "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus";
            case UNKNOWN: return "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus";
            default: return "?";
          }
        }
        public String getDefinition() {
          switch (this) {
            case ACTIVE: return "The credential may be considered valid for use";
	        case INACTIVE: return "The credential may not be considered valid for use";
	        case ISSUEDINERROR: return "The credential was mistakenly assigned and should not be considered valid for use";
	        case REVOKED: return "The credential was revoked by the issuing organization and should not be considered valid for use";
	        case PENDING: return "The credential has not been officially assigned. It may or may not be considered valid for use";
	        case UNKNOWN: return "The status of this credential is unknown. It may or may not be considered valid for use";
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
	        case ACTIVE: return "active";
	        case INACTIVE: return "inactive";
	        case ISSUEDINERROR: return "issuedinerror";
	        case REVOKED: return "revoked";
	        case PENDING: return "pending";
	        case UNKNOWN: return "unknown";
            default: return "?";
          }
        }
    }
	
	public static class IdentifierStatusEnumFactory implements EnumFactory<IdentifierStatus> {
	    public IdentifierStatus fromCode(String codeString) throws IllegalArgumentException {
	      if (codeString == null || "".equals(codeString))
	            if (codeString == null || "".equals(codeString))
	                return null;
	        if ("active".equals(codeString))
	          return IdentifierStatus.ACTIVE;
	        if ("inactive".equals(codeString))
	          return IdentifierStatus.INACTIVE;
	        if ("issuedinerror".equals(codeString))
	          return IdentifierStatus.ISSUEDINERROR;
	        if ("revoked".equals(codeString))
	          return IdentifierStatus.REVOKED;
	        if ("pending".equals(codeString))
	          return IdentifierStatus.PENDING;
	        if ("unknown".equals(codeString))
		      return IdentifierStatus.UNKNOWN;
	        throw new IllegalArgumentException("Unknown IdentifierStatus code '"+codeString+"'");
	        }
	        public Enumeration<IdentifierStatus> fromType(Base code) throws FHIRException {
	          if (code == null)
	            return null;
	          if (code.isEmpty())
	            return new Enumeration<IdentifierStatus>(this);
	          String codeString = ((PrimitiveType) code).asStringValue();
	          if (codeString == null || "".equals(codeString))
	            return null;
	        if ("active".equals(codeString))
	          return new Enumeration<IdentifierStatus>(this, IdentifierStatus.ACTIVE);
	        if ("inactive".equals(codeString))
	          return new Enumeration<IdentifierStatus>(this, IdentifierStatus.INACTIVE);
	        if ("issuedinerror".equals(codeString))
	          return new Enumeration<IdentifierStatus>(this, IdentifierStatus.ISSUEDINERROR);
	        if ("revoked".equals(codeString))
	          return new Enumeration<IdentifierStatus>(this, IdentifierStatus.REVOKED);
	        if ("pending".equals(codeString))
	          return new Enumeration<IdentifierStatus>(this, IdentifierStatus.PENDING);
	        if ("unknown".equals(codeString))
		      return new Enumeration<IdentifierStatus>(this, IdentifierStatus.UNKNOWN);
	        throw new FHIRException("Unknown IdentifierStatus code '"+codeString+"'");
	        }
	    public String toCode(IdentifierStatus code) {
	      if (code == IdentifierStatus.ACTIVE)
	        return "active";
	      if (code == IdentifierStatus.INACTIVE)
	        return "inactive";
	      if (code == IdentifierStatus.ISSUEDINERROR)
	        return "issuedinerror";
	      if (code == IdentifierStatus.REVOKED)
	        return "revoked";
	      if (code == IdentifierStatus.PENDING)
	        return "pending";
	      if (code == IdentifierStatus.UNKNOWN)
		    return "unknown";
	      return "?";
	      }
	    public String toSystem(IdentifierStatus code) {
	      return code.getSystem();
	      }
	    }
	
	/**
	 * The status of this identifier
	 */
	@Child(name="status", type = {CodeType.class},  min=1, max=1, modifier=true, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/identifier-status", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Status")
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-credentialstatus")
	private Enumeration<IdentifierStatus> status;
	
	/**
	 * Constructor
	 */
	public VhDirIdentifier() {
		super();
	}
	
	/**
     * @return {@link #status} (The status of this identifier.). This is the underlying object with id, value and extensions. The accessor "getStatus" gives direct access to the value
     */
    public Enumeration<IdentifierStatus> getStatusElement() { 
      if (this.status == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Identifier.status");
        else if (Configuration.doAutoCreate())
          this.status = new Enumeration<IdentifierStatus>(new IdentifierStatusEnumFactory()); // bb
      return this.status;
    }

    public boolean hasStatusElement() { 
      return this.status != null && !this.status.isEmpty();
    }

    public boolean hasStatus() { 
      return this.status != null && !this.status.isEmpty();
    }

    /**
     * @param value {@link #status} (The status of this identifier.). This is the underlying object with id, value and extensions. The accessor "getUse" gives direct access to the value
     */
    public Identifier setStatusElement(Enumeration<IdentifierStatus> value) { 
      this.status = value;
      return this;
    }

    /**
     * @return The purpose of this identifier.
     */
    public IdentifierStatus getStatus() { 
      return this.status == null ? null : this.status.getValue();
    }

    /**
     * @param value The purpose of this identifier.
     */
    public Identifier setStatus(IdentifierStatus value) { 
      if (value == null)
        this.status = null;
      else {
        if (this.status == null)
          this.status = new Enumeration<IdentifierStatus>(new IdentifierStatusEnumFactory());
        this.status.setValue(value);
      }
      return this;
    }
}
