package com.esacinc.spd.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;

@ResourceDef(name="Organization", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-organization")
public class VhDirOrganization extends DomainResource {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the VhDirIdentifier
	 */
	@Child(name="identifier", type = {VhDirIdentifier.class}, order=1, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Identifier for the organization")
	private List<VhDirIdentifier> identifier;
	
	/**
	 * Add the organization description extension
	 */
	@Child(name="description", type = {StringType.class}, order=2, min=0, max=1, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/org-description", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Description of the organization")
	private StringType description;
	
	/**
	 * Add the digital certificate
	 */
	@Child(name="digitalcertificate", type = {VhDirDigitalCertificate.class}, order=3, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/digitalcertificate", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Certificate for the organization")
	private List<VhDirDigitalCertificate> digitalcertficate;
	
	/**
	 * Add the qualification
	 */
	@Child(name="qualification", type = {VhDirQualification.class}, order=4, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/qualification", definedLocally=false, isModifier=false)
	@Description(shortDefinition="Qualification for the organization")
	private List<VhDirQualification> qualification;
	
	/**
     * Whether the organization's record is still in active use.
     */
    @Child(name = "active", type = {BooleanType.class}, order=5, min=0, max=1, modifier=true, summary=true)
    @Description(shortDefinition="Whether the organization's record is still in active use", formalDefinition="Whether the organization's record is still in active use." )
    protected BooleanType active;
    
    /**
     * The kind(s) of organization that this is.
     */
    @Child(name = "type", type = {CodeableConcept.class}, order=6, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Kind of organization", formalDefinition="The kind(s) of organization that this is." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/organization-type")
    protected List<CodeableConcept> type;
    
    /**
     * A name associated with the organization.
     */
    @Child(name = "name", type = {StringType.class}, order=7, min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Name used for the organization", formalDefinition="A name associated with the organization." )
    protected StringType name;
    
    /**
     * Add the overridden alias
     */
    @Child(name="alias", type = {VhDirAlias.class}, order=8, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
	@Description(shortDefinition="Alias for the organization")
	private List<VhDirAlias> alias;
    
	/**
	 * Add the overridden ContactPoint for telecom
	 */
    @Child(name = "telecom", type = {VhDirContactPoint.class}, order=9, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="A contact detail for the organization", formalDefinition="A contact detail for the organization." )
    protected List<VhDirContactPoint> telecom;
	
	/**
	 * Add the overridden Address for address
	 */
	@Child(name = "address", type = {VhDirAddress.class}, order=10, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="An address for the organization", formalDefinition="An address for the organization." )
    protected List<VhDirAddress> address;
	
	/**
     * The organization of which this organization forms a part.
     */
    @Child(name = "partOf", type = {VhDirOrganization.class}, order=11, min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="The organization of which this organization forms a part", formalDefinition="The organization of which this organization forms a part." )
    protected Reference partOf;

    /**
     * The actual object that is the target of the reference (The organization of which this organization forms a part.)
     */
    protected VhDirOrganization partOfTarget;
	
	/**
     * Contact for the organization for a certain purpose.
     */
    @Child(name = "contact", type = {}, order=12, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="Contact for the organization for a certain purpose", formalDefinition="Contact for the organization for a certain purpose." )
    protected List<OrganizationContactComponent> contact;
    
	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(identifier, description, name, type, active,
        		alias, telecom, address);
    }
    
    // Getter for description
    public String getDescription() {
    	return this.description == null ? null : this.description.getValue();
    }
    
    // Setter for description
    public VhDirOrganization setDescription(String value) { 
        if (Utilities.noString(value))
          this.description = null;
        else {
          if (this.description == null)
            this.description = new StringType();
          this.description.setValue(value);
        }
        return this;
    }

	public List<VhDirDigitalCertificate> getDigitalcertficate() {
		return digitalcertficate;
	}

	public void setDigitalcertficate(List<VhDirDigitalCertificate> digitalcertficate) {
		this.digitalcertficate = digitalcertficate;
	}
	
	public VhDirOrganization addDigitalcertficate(VhDirDigitalCertificate t) {
	    if (t == null)
	      return this;
	    if (this.digitalcertficate == null)
	      this.digitalcertficate = new ArrayList<VhDirDigitalCertificate>();
	    this.digitalcertficate.add(t);
	    return this;
	}
	
	public List<VhDirQualification> getQualification() {
		return qualification;
	}

	public void setQualification(List<VhDirQualification> qualification) {
		this.qualification = qualification;
	}
	
	public VhDirOrganization addQualification(VhDirQualification t) {
	    if (t == null)
	      return this;
	    if (this.qualification == null)
	      this.qualification = new ArrayList<VhDirQualification>();
	    this.qualification.add(t);
	    return this;
	}

	public List<VhDirContactPoint> getTelecom() {
		return telecom;
	}

	public void setTelecom(List<VhDirContactPoint> telecom) {
		this.telecom = telecom;
	}
	
	public VhDirOrganization addTelecom(VhDirContactPoint t) {
	    if (t == null)
	      return this;
	    if (this.telecom == null)
	      this.telecom = new ArrayList<VhDirContactPoint>();
	    this.telecom.add(t);
	    return this;
	}
	
	/**
     * @return {@link #address} (An address for the organization.)
     */
    public List<VhDirAddress> getAddress() { 
      if (this.address == null)
        this.address = new ArrayList<VhDirAddress>();
      return this.address;
    }

    /**
     * @return Returns a reference to <code>this</code> for easy method chaining
     */
    public VhDirOrganization setAddress(List<VhDirAddress> theAddress) { 
      this.address = theAddress;
      return this;
    }

    public boolean hasAddress() { 
      if (this.address == null)
        return false;
      for (Address item : this.address)
        if (!item.isEmpty())
          return true;
      return false;
    }

    public VhDirAddress addAddress() { //3
      VhDirAddress t = new VhDirAddress();
      if (this.address == null)
        this.address = new ArrayList<VhDirAddress>();
      this.address.add(t);
      return t;
    }

    public VhDirOrganization addAddress(VhDirAddress t) { //3
      if (t == null)
        return this;
      if (this.address == null)
        this.address = new ArrayList<VhDirAddress>();
      this.address.add(t);
      return this;
    }

    /**
     * @return The first repetition of repeating field {@link #address}, creating it if it does not already exist
     */
    public Address getAddressFirstRep() { 
      if (getAddress().isEmpty()) {
        addAddress();
      }
      return getAddress().get(0);
    }

	@Override
	public DomainResource copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceType getResourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * @return {@link #active} (Whether the organization's record is still in active use.). This is the underlying object with id, value and extensions. The accessor "getActive" gives direct access to the value
     */
    public BooleanType getActiveElement() { 
      if (this.active == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Organization.active");
        else if (Configuration.doAutoCreate())
          this.active = new BooleanType(); // bb
      return this.active;
    }

    public boolean hasActiveElement() { 
      return this.active != null && !this.active.isEmpty();
    }

    public boolean hasActive() { 
      return this.active != null && !this.active.isEmpty();
    }

    /**
     * @param value {@link #active} (Whether the organization's record is still in active use.). This is the underlying object with id, value and extensions. The accessor "getActive" gives direct access to the value
     */
    public VhDirOrganization setActiveElement(BooleanType value) { 
      this.active = value;
      return this;
    }

    /**
     * @return Whether the organization's record is still in active use.
     */
    public boolean getActive() { 
      return this.active == null || this.active.isEmpty() ? false : this.active.getValue();
    }

    /**
     * @param value Whether the organization's record is still in active use.
     */
    public VhDirOrganization setActive(boolean value) { 
        if (this.active == null)
          this.active = new BooleanType();
        this.active.setValue(value);
      return this;
    }
    
    /**
     * @return {@link #type} (The kind(s) of organization that this is.)
     */
    public List<CodeableConcept> getType() { 
      if (this.type == null)
        this.type = new ArrayList<CodeableConcept>();
      return this.type;
    }

    /**
     * @return Returns a reference to <code>this</code> for easy method chaining
     */
    public VhDirOrganization setType(List<CodeableConcept> theType) { 
      this.type = theType;
      return this;
    }

    public boolean hasType() { 
      if (this.type == null)
        return false;
      for (CodeableConcept item : this.type)
        if (!item.isEmpty())
          return true;
      return false;
    }

    public CodeableConcept addType() { //3
      CodeableConcept t = new CodeableConcept();
      if (this.type == null)
        this.type = new ArrayList<CodeableConcept>();
      this.type.add(t);
      return t;
    }

    public VhDirOrganization addType(CodeableConcept t) { //3
      if (t == null)
        return this;
      if (this.type == null)
        this.type = new ArrayList<CodeableConcept>();
      this.type.add(t);
      return this;
    }
    
    /**
     * @return {@link #name} (A name associated with the organization.). This is the underlying object with id, value and extensions. The accessor "getName" gives direct access to the value
     */
    public StringType getNameElement() { 
      if (this.name == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Organization.name");
        else if (Configuration.doAutoCreate())
          this.name = new StringType(); // bb
      return this.name;
    }

    public boolean hasNameElement() { 
      return this.name != null && !this.name.isEmpty();
    }

    public boolean hasName() { 
      return this.name != null && !this.name.isEmpty();
    }

    /**
     * @param value {@link #name} (A name associated with the organization.). This is the underlying object with id, value and extensions. The accessor "getName" gives direct access to the value
     */
    public VhDirOrganization setNameElement(StringType value) { 
      this.name = value;
      return this;
    }

    /**
     * @return A name associated with the organization.
     */
    public String getName() { 
      return this.name == null ? null : this.name.getValue();
    }

    /**
     * @param value A name associated with the organization.
     */
    public VhDirOrganization setName(String value) { 
      if (Utilities.noString(value))
        this.name = null;
      else {
        if (this.name == null)
          this.name = new StringType();
        this.name.setValue(value);
      }
      return this;
    }
	
	public List<VhDirIdentifier> getIdentifier() {
		return identifier;
	}

	public void setIdentifier(List<VhDirIdentifier> identifier) {
		this.identifier = identifier;
	}
	
	public VhDirOrganization addIdentifier(VhDirIdentifier t) { //3
	    if (t == null)
	      return this;
	    if (this.identifier == null)
	      this.identifier = new ArrayList<VhDirIdentifier>();
	    this.identifier.add(t);
	    return this;
	}
	
	/**
     * @return {@link #partOf} (The organization of which this organization forms a part.)
     */
    public Reference getPartOf() { 
      if (this.partOf == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Organization.partOf");
        else if (Configuration.doAutoCreate())
          this.partOf = new Reference(); // cc
      return this.partOf;
    }

    public boolean hasPartOf() { 
      return this.partOf != null && !this.partOf.isEmpty();
    }

    public VhDirOrganization setPartOf(Reference value) { 
      this.partOf = value;
      return this;
    }

    public VhDirOrganization getPartOfTarget() { 
      if (this.partOfTarget == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Organization.partOf");
        else if (Configuration.doAutoCreate())
          this.partOfTarget = new VhDirOrganization(); // aa
      return this.partOfTarget;
    }

    public VhDirOrganization setPartOfTarget(VhDirOrganization value) { 
      this.partOfTarget = value;
      return this;
    }
    
    public VhDirOrganization setAlias(List<VhDirAlias> theAlias) { 
      this.alias = theAlias;
      return this;
    }

    public boolean hasAlias() { 
      if (this.alias == null)
        return false;
      for (VhDirAlias item : this.alias)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #alias} (A list of alternate names that the organization is known as, or was known as in the past.)
     */
    public VhDirAlias addAliasElement() {//2 
    	VhDirAlias t = new VhDirAlias();
      if (this.alias == null)
        this.alias = new ArrayList<VhDirAlias>();
      this.alias.add(t);
      return t;
    }

    /**
     * @param value {@link #alias} (A list of alternate names that the organization is known as, or was known as in the past.)
     */
    public VhDirOrganization addAlias(VhDirAlias value) { //1
      if (this.alias == null)
        this.alias = new ArrayList<VhDirAlias>();
      this.alias.add(value);
      return this;
    }
    
    /**
     * @return {@link #contact} (Contact for the organization for a certain purpose.)
     */
    public List<OrganizationContactComponent> getContact() { 
      if (this.contact == null)
        this.contact = new ArrayList<OrganizationContactComponent>();
      return this.contact;
    }

    public VhDirOrganization setContact(List<OrganizationContactComponent> theContact) { 
      this.contact = theContact;
      return this;
    }

    public boolean hasContact() { 
      if (this.contact == null)
        return false;
      for (OrganizationContactComponent item : this.contact)
        if (!item.isEmpty())
          return true;
      return false;
    }

    public OrganizationContactComponent addContact() { //3
      OrganizationContactComponent t = new OrganizationContactComponent();
      if (this.contact == null)
        this.contact = new ArrayList<OrganizationContactComponent>();
      this.contact.add(t);
      return t;
    }

    public VhDirOrganization addContact(OrganizationContactComponent t) { //3
      if (t == null)
        return this;
      if (this.contact == null)
        this.contact = new ArrayList<OrganizationContactComponent>();
      this.contact.add(t);
      return this;
    }

    /**
     * @return The first repetition of repeating field {@link #contact}, creating it if it does not already exist
     */
    public OrganizationContactComponent getContactFirstRep() { 
      if (getContact().isEmpty()) {
        addContact();
      }
      return getContact().get(0);
    }

}
