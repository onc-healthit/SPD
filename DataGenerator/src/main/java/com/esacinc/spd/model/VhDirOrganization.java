package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.utilities.Utilities;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;

@ResourceDef(name="Organization", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-organization")
public class VhDirOrganization extends Organization {
	private static final long serialVersionUID = 1L;
    
	/**
	 * Add the organization description extension
	 */
	@Child(name="description", type = {StringType.class})
	@Extension(url="http://hl7.org/fhir/uv/vhdir/StructureDefinition/org-description", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Description of the organization")
	private StringType description;
	
	/**
     * It is important to override the isEmpty() method, adding a check for any
     * newly added fields. 
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(description);
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
}
