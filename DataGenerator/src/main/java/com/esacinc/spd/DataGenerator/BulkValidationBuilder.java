package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.hl7.fhir.r4.model.Timing.UnitsOfTime;
import org.hl7.fhir.r4.model.VerificationResult.Status;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultPrimarySourceComponent;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent;

import com.esacinc.spd.model.VhDirValidation;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;

public class BulkValidationBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirValidations and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirValidation> getValidations(Connection connection) throws SQLException, ParseException {
		List<VhDirValidation> validations = new ArrayList<VhDirValidation>();
		int cnt = 0;
	       ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_validation WHERE validation_id > " + BulkDataApp.FROM_ID_VALIDATIONS + " ORDER BY validation_id",null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			//System.out.println("Creating location for id " + resultSet.getInt("location_id"));
			VhDirValidation val = new VhDirValidation();
		
			// set the id
			int valId = resultSet.getInt("validation_id");
			val.setId(resultSet.getString("validation_id"));
			ErrorReport.setCursor("VhDirValidation", val.getId());

			val.setText(ResourceFactory.makeNarrative("VerificationResult (id: " + valId + ")"));

			val.setNeed(ResourceFactory.getCodeableConcept(resultSet.getInt("need_cc_id"),connection));
			val.addTargetLocation(resultSet.getString("target_location"));
			val.setStatusDate(resultSet.getDate("status_date")); 
			val.setValidationType(ResourceFactory.getCodeableConcept(resultSet.getInt("validation_type_cc_id"),connection));
			val.setLastPerformed(resultSet.getDate("last_performed"));	
			val.setNextScheduled(resultSet.getDate("next_scheduled"));
			val.setFailureAction(ResourceFactory.getCodeableConcept(resultSet.getInt("failure_action_cc_id"),connection));
			

			handleStatus(resultSet, val);
			
			handleTargets(connection,val, valId);
			
			handleValidationProcesses(connection, val, valId);

			handleFrequency(connection, resultSet, val,valId);
			
			handlePrimarySources(connection, val, valId);
			
			handleAttestation(connection, resultSet, val);
			
			handleValidators(connection, val, valId);
			
			validations.add(val);
			
			cnt++;
		}
		System.out.println("Made " + validations.size() + " validations");
		return validations;
	}

	
	/**
	 * Handles astatus  for Validations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handleStatus(ResultSet resultset, VhDirValidation val){
		
		try {
			val.setStatus(Status.fromCode(resultset.getString("status")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			val.setStatus(Status.NULL);
			ErrorReport.writeWarning("VhDirValidation", val.getId(), "unrecognized status ", e.getMessage());

		}

	}

	/**
	 * Handles all the elements of the Validation Processes  for Locations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handleValidationProcesses(Connection connection, VhDirValidation val, int valId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from fhir_codeable_concept where validation_process_id = ?", valId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			val.addValidationProcess(cc);
		}
	}

	/**
	 * Handle frequency for Validations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handleFrequency(Connection connection, ResultSet resultSet, VhDirValidation val, int valId) throws SQLException {
		// TODO wow...need to model frequency in db first
		// For now, assume the frequency value in the db is a ;-delimited string containing frequency value and unit. e.g.   "1;wk"
		Duration dur = new Duration();
		Timing tim = new Timing();
		String strFreq = resultSet.getString("frequency");
		String delim = ";";
		// Check to see if the db string is pipe delimited rather than semicolon delimited...
		if (strFreq.indexOf("|") > -1) {
			delim = "|";
		}
		try {
			if (strFreq != null && !strFreq.isEmpty()) {
				String[] tokens = strFreq.split(delim);
				dur.setValue(Double.valueOf(tokens[0]));
				dur.setUnit(tokens[1]);
				TimingRepeatComponent trc = new TimingRepeatComponent();
				trc.setDuration(Double.valueOf(tokens[0]));
			    trc.setDurationUnit(UnitsOfTime.fromCode(tokens[1]));
			    tim.setRepeat(trc);
			}
			val.setFrequency(tim);
		}
		catch (Exception e) {
			System.err.println("Error parsing frequency: " + strFreq + ", " + e.getMessage());
			ErrorReport.writeError("VhDirValidation", ""+valId, "Frequency error", "Error parsing frequency of " + strFreq + ". Expect a string of form 'val;unit'");
		}
	}

	/**
	 * Handles all the primary sources  for Validations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handlePrimarySources(Connection connection, VhDirValidation val, int valId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from primary_source where validation_id = ?", valId);
		while(resultset.next()) {
			VerificationResultPrimarySourceComponent ps = ResourceFactory.getPrimarySource(resultset,connection);
			val.addPrimarySource(ps);
		} 
	}

	/**
	 * Handles the Attestation  for Validations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handleAttestation(Connection connection, ResultSet resultset,  VhDirValidation val) throws SQLException {
	    ResultSet attset = DatabaseUtil.runQuery(connection, "SELECT * from attestation where attestation_id = ?", resultset.getInt("attestation_id"));
		while(attset.next()) {
			VerificationResultAttestationComponent att = ResourceFactory.getAttestation(attset, connection);
			val.setAttestation(att);
			break;  // we're only exptecting one
		} 
	}

	/**
	 * Handles the Validators  for Validations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handleValidators(Connection connection,  VhDirValidation val, int valId) throws SQLException {
	    ResultSet valset = DatabaseUtil.runQuery(connection, "SELECT * from validator where validation_id = ?", valId);
		while(valset.next()) {
			VerificationResultValidatorComponent validator = ResourceFactory.getValidator(valset, connection);
			val.addValidator(validator);
		} 
	}

	/**
	 * Handles the Targets  for Validations
	 * 
	 * @param connection
	 * @param val
	 * @param valId
	 * @throws SQLException
	 */
	private void handleTargets(Connection connection, VhDirValidation val, int valId) throws SQLException {
	    ResultSet valset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where validation_target_id = ?", valId);
		while(valset.next()) {
			Reference ref = ResourceFactory.getResourceReference(valset, connection);
			val.addTarget(ref);
		} 
	}
}
