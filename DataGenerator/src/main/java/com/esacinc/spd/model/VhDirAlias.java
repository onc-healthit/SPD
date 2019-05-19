package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Period;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;
//import ca.uhn.fhir.model.api.annotation.Extension;

@DatatypeDef(name="vhdir-alias")
public class VhDirAlias extends Extension  {
	private static final long serialVersionUID = 1L;

	/**
     * A coded type for the alias that can be used to determine which alias to use for a specific purpose.
     */
    @Child(name = "type", type = {CodeableConcept.class}, order=1, min=0, max=1, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/org-alias-type", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Description of alias", formalDefinition="A coded type for the alias that can be used to determine which alias to use for a specific purpose." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-organizationdemographics")
    protected CodeableConcept type;
    
    /**
     * The portion of the alias typically relevant to the user and which is unique within the context of the system.
     */
   // @Child(name = "value", type = {StringType.class}, order=3, min=0, max=1, modifier=false, summary=true)
   // @Description(shortDefinition="The value that is unique", formalDefinition="The portion of the alias typically relevant to the user and which is unique within the context of the system." )
  //  protected StringType value;

    /**
     * Time period during which alias is/was valid for use.
     */
    @Child(name = "period", type = {Period.class}, order=4, min=0, max=1, modifier=false, summary=true)
	//@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/org-alias-period", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Time period when id is/was valid for use", formalDefinition="Time period during which alias is/was valid for use." )
    protected Period period;
    
    /**
     * Constructor
     */
      public VhDirAlias() {
        super();
      }
      
    /**
     * @return {@link #type} (A coded type for the alias that can be used to determine which alias to use for a specific purpose.)
     */
    public CodeableConcept getType() { 
      if (this.type == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Alias.type");
        else if (Configuration.doAutoCreate())
          this.type = new CodeableConcept();
      return this.type;
    }

    public boolean hasType() { 
        return this.type != null && !this.type.isEmpty();
    }

    /**
     * @param value {@link #type} (A coded type for the alias that can be used to determine which alias to use for a specific purpose.)
     */
    public VhDirAlias setType(CodeableConcept value) { 
      this.type = value;
      return this;
    }
      
    /**
     * @return {@link #value} (The portion of the alias typically relevant to the user and which is unique within the context of the system.). This is the underlying object with id, value and extensions. The accessor "getValue" gives direct access to the value
     */
    /*
    public StringType getValueElement() { 
      if (this.value == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create alias.value");
        else if (Configuration.doAutoCreate())
          this.value = new StringType();
      return this.value;
    }

    public boolean hasValueElement() { 
        return this.value != null && !this.value.isEmpty();
    }

    public boolean hasValue() { 
        return this.value != null && !this.value.isEmpty();
    }
*/
    /**
     * @param value {@link #value} (The portion of the alias typically relevant to the user and which is unique within the context of the system.). This is the underlying object with id, value and extensions. The accessor "getValue" gives direct access to the value
     */
//    public VhDirAlias setValueElement(StringType value) { 
//        this.value = value;
//        return this;
//    }

    /**
     * @return The portion of the alias typically relevant to the user and which is unique within the context of the system.
     */
 //   public StringType getValue() { 
 //       return this.value == null ? null : this.value;
 //   }

    /**
     * @param value The portion of the alias typically relevant to the user and which is unique within the context of the system.
     */
    /*
    public VhDirAlias setValue(String value) { 
        if (Utilities.noString(value))
          this.value = null;
        else {
          if (this.value == null)
            this.value = new StringType();
          this.value.setValue(value);
        }
        return this;
    }
    */
    /**
     * @return {@link #period} (Time period during which alias is/was valid for use.)
     */
    public Period getPeriod() { 
      if (this.period == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create Alias.period");
        else if (Configuration.doAutoCreate())
          this.period = new Period(); // cc
      return this.period;
    }

    public boolean hasPeriod() { 
      return this.period != null && !this.period.isEmpty();
    }

    /**
     * @param value {@link #period} (Time period during which alias is/was valid for use.)
     */
    public VhDirAlias setPeriod(Period value) { 
      this.period = value;
      return this;
    }
    
    public String fhirType() {
        return "VhDirAlias";
    }

    public VhDirAlias copy() {
    	VhDirAlias dst = new VhDirAlias();
        copyValues(dst);
        dst.type = type == null ? null : type.copy();
        dst.value = value == null ? null : value.copy();
        dst.period = period == null ? null : period.copy();
        return dst;
    }


    @Override
    public boolean equalsDeep(Base other_) {
        if (!super.equalsDeep(other_))
          return false;
        if (!(other_ instanceof VhDirAlias))
          return false;
        VhDirAlias o = (VhDirAlias) other_;
        return compareDeep(type, o.type, true) && compareDeep(value, o.value, true) &&
        		compareDeep(period, o.period, true);
    }
/*
    @Override
    public boolean equalsShallow(Base other_) {
      if (!super.equalsShallow(other_))
          return false;
      if (!(other_ instanceof VhDirAlias))
          return false;
      VhDirAlias o = (VhDirAlias) other_;
      return compareValues(value, o.value, true);
    }
*/
    /**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    public boolean isEmpty() {
        return super.isEmpty() && ca.uhn.fhir.util.ElementUtil.isEmpty(type, value, period);
    }

}
