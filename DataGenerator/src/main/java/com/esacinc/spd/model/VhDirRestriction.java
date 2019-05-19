package com.esacinc.spd.model;

import org.hl7.fhir.r4.model.Consent;

import ca.uhn.fhir.model.api.annotation.ResourceDef;

/**
 * The VhDir restriciton profile builds on Consent, and only removes fields. No new extensions are defined.
 * 
 * 
 * @author dandonahue
 *
 */
@ResourceDef(name="vhdir-restriction", profile="http://hl7.org/fhir/uv/vhdir/StructureDefinition/vhdir-restriction")
public class VhDirRestriction extends Consent { 
	private static final long serialVersionUID = 1L;
    


   
}
