package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.VerificationResult.Status;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent;

import com.esacinc.spd.model.VhDirPrimarySource;
import com.esacinc.spd.model.VhDirValidation;
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
		
		String sql = "SELECT * FROM vhdir_validation";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			//System.out.println("Creating location for id " + resultSet.getInt("location_id"));
			VhDirValidation val = new VhDirValidation();
		
			// set the id
			int valId = resultSet.getInt("validation_id");
			val.setId(resultSet.getString("validation_id"));
			val.setNeed(ResourceFactory.getCodeableConcept(resultSet.getInt("need_cc_id"),connection));
			val.addTargetLocation(resultSet.getString("target_location"));
			val.setStatusDate(resultSet.getDate("status_date")); 
			val.setValidationType(ResourceFactory.getCodeableConcept(resultSet.getInt("validation_type_cc_id"),connection));
			val.setLastPerformed(resultSet.getDate("last_performed"));	
			val.setNextScheduled(resultSet.getDate("next_scheduled"));
			val.setFailureAction(ResourceFactory.getCodeableConcept(resultSet.getInt("failure_action_cc_id"),connection));
			

			handleStatus(resultSet, val);
			
			handleValidationProcesses(connection, val, valId);

			handleFrequency(connection, resultSet, val,valId);
			
			handlePrimarySources(connection, val, valId);
			
			handleAttestation(connection, resultSet, val);
			
			handleValidators(connection, resultSet, val);
			
			validations.add(val);
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
		String strSql = "SELECT * from fhir_codeable_concept where validation_process_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, valId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		Timing freq = new Timing();
		String strFreq = resultSet.getString("frequency");
		val.setFrequency(freq);
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
		String strSql = "SELECT * from primary_source where validation_status_cc_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, valId);
		ResultSet resultset = sqlStatement.executeQuery();
		while(resultset.next()) {
			VhDirPrimarySource ps = ResourceFactory.getPrimarySource(resultset,connection);
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
		String strSql = "SELECT * from attestation where attestation_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, resultset.getInt("attestation_id"));
		ResultSet attset = sqlStatement.executeQuery();
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
	private void handleValidators(Connection connection, ResultSet resultset,  VhDirValidation val) throws SQLException {
		String strSql = "SELECT * from validator where validation_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, resultset.getInt("validation_id"));
		ResultSet valset = sqlStatement.executeQuery();
		while(valset.next()) {
			VerificationResultValidatorComponent validator = ResourceFactory.getValidator(valset, connection);
			val.addValidator(validator);
		} 
	}

}
