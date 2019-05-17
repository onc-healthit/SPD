package com.esacinc.spd.model;

import java.lang.annotation.Annotation;

import org.hl7.fhir.r4.model.BackboneElement;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Configuration;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.api.ICompositeType;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.api.annotation.Description;

@DatatypeDef(name="VhDirAlias_lite")
public class VhDirAlias_lite extends BackboneElement  {
	private static final long serialVersionUID = 1L;

	/**
     * A coded type for the alias that can be used to determine which alias to use for a specific purpose.
     */
    @Child(name = "type", type = {CodeableConcept.class},  min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Description of alias", formalDefinition="A coded type for the alias that can be used to determine which alias to use for a specific purpose." )
    protected CodeableConcept type;
    
    /**
     * The portion of the alias typically relevant to the user and which is unique within the context of the system.
     */
    @Child(name = "value", type = {StringType.class}, min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="The value that is unique", formalDefinition="The portion of the alias typically relevant to the user and which is unique within the context of the system." )
    protected StringType value;

    /**
     * Time period during which alias is/was valid for use.
     */
    @Child(name = "period", type = {Period.class}, min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Time period when id is/was valid for use", formalDefinition="Time period during which alias is/was valid for use." )
    protected Period period;
    
    /**
     * Constructor
     */
      public VhDirAlias_lite() {
        super();
      }
      
    /**
     * @return {@link #type} (A coded type for the alias that can be used to determine which alias to use for a specific purpose.)
     */
    public CodeableConcept getType() { 
      return this.type;
    }

    public boolean hasType() { 
        return this.type != null && !this.type.isEmpty();
    }

    /**
     * @param value {@link #type} (A coded type for the alias that can be used to determine which alias to use for a specific purpose.)
     */
    public void setType(CodeableConcept value) { 
      this.type = value;
    }
      
  
    /**
     * @return The portion of the alias typically relevant to the user and which is unique within the context of the system.
     */
    public StringType getValue() { 
        return this.value;
    }

    /**
     * @param value The portion of the alias typically relevant to the user and which is unique within the context of the system.
     */
    public void setValue(StringType val) { 
        this.value = val;
     }
    
    /**
     * @return {@link #period} (Time period during which alias is/was valid for use.)
     */
    public Period getPeriod() { 
       return this.period;
    }

    public boolean hasPeriod() { 
      return this.period != null && !this.period.isEmpty();
    }

    /**
     * @param value {@link #period} (Time period during which alias is/was valid for use.)
     */
    public VhDirAlias_lite setPeriod(Period value) { 
      this.period = value;
      return this;
    }
    
  
    public VhDirAlias_lite copy() {
    	VhDirAlias_lite dst = new VhDirAlias_lite();
        copyValues(dst);
        dst.type = type == null ? null : type.copy();
        dst.value = value == null ? null : value.copy();
        dst.period = period == null ? null : period.copy();
        return dst;
    }



}
