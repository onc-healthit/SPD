package com.esacinc.spd.util;

import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirTelecom;
import org.hl7.fhir.r4.model.InsurancePlan.*;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class has static public methods for return various data associated with Insurance Plans from the database.
 * Since there is so much structure and data around Insurance Plans, a separate factory class was indicated.
 * 
 * The Insurance Plan data hierarchy is complex and full of lists of things.  The hierarchy is presented below,
 * with each item representing a list of items where an item is either a resource reference, component, or codeable concept.
 * Many of the component type are already modeled as HAPI FHIR components, so it is just a matter of populating instnaces of
 * them from the appropriate database tables.
 * 
 * In the hierarchy below, only the list items are shown. 
 * 
 * If class methods to gather the various items in the hierarch are not present in this class, then they can be found in
 * the BulkInsurancePlanBuilder class.
 * 
 * 		Insurance Plan                      (VhDirInsurancePlan resource)
 * 			Usage Restrictions              (VhDirRestriction resource references)
 * 			Identifiers                     (VhDirIdentifier components)
 * 			Plan Types                      (CodeableConcepts)
 * 			Plan Aliases                    (Strings)
 * 			Coverage Areas                  (VhDirLocation resource references)
 * 			Contacts                        (InsurancePlanContactComponent components  - HAPI)
 * 			Endpoints                       (VhDirEndpoint resource references)
 * 			Networks                        (VhDirNetwork resource references)
 * 			Coverages                       (InsurancePlanCoverageComponent components - HAPI)
 * 				Coverage Networks           (VhDirNetwork resource references)
 * 				Coverage Benefits           (CoverageBenefitComponent components - HAPI)
 * 					Limits                  (CoverageBenefitLimitComponent components - HAPI)
 * 			Plans                           (InsurancePlanPlanComponent components - HAPI)
 * 				Identifiers                 (VhDirIdentifier components)
 * 				Coverage Areas              (VhDirLocation resource references)
 *              Networks                    (VhDirNetwork resource references)
 *              General Costs               (InsurancePlanPlanGeneralCostComponent components - HAPI)
 * 				Specific Costs              (InsurancePlanPlanSpecificCostComponent components - HAPI)
 * 					Cost Benefits           (PlanBenefitComponent components - HAPI)
 * 						Costs               (PlanBenefitCostComponent components - HAPI)
 *							Cost Qualifiers (CodeableConcepts)
 *
 *
 * @author dandonahue
 *
 */
public class InsurancePlanFactory {

	//----------------------------------------------------------------------------------------------------------------------
	// Public methods
	//----------------------------------------------------------------------------------------------------------------------

	public InsurancePlanFactory() { }

	public static InsurancePlanCoverageComponent getCoverage(ResultSet resultset, Connection connection) throws SQLException {
		InsurancePlanCoverageComponent cov = new InsurancePlanCoverageComponent();
		cov.setId(resultset.getString("coverage_id"));
		cov.setType(ResourceFactory.getCodeableConcept(resultset.getInt("type_cc_id"),connection));
		// get coverage networks
		ResultSet refResultSet = DatabaseUtil.runQuery(connection, "select * from resource_reference where coverage_network_id =?", resultset.getInt("coverage_id"));
		while (refResultSet.next()) {
			Reference nw = ResourceFactory.getResourceReference(refResultSet,connection);
			cov.addNetwork(nw);
		}
		// get coverage benefits
		ResultSet benResultSet = DatabaseUtil.runQuery(connection, "select * from coverage_benefit where coverage_id =?", resultset.getInt("coverage_id"));
		while (benResultSet.next()) {
			cov.addBenefit(getCoverageBenefit(benResultSet, connection));
		}
		return cov;
	}

	public static InsurancePlanPlanComponent getPlan(ResultSet resultset, Connection connection) throws SQLException {
		InsurancePlanPlanComponent plan = new InsurancePlanPlanComponent();
		int planId = resultset.getInt("plan_id");
		plan.setId(resultset.getString("plan_id"));
		plan.setType(ResourceFactory.getCodeableConcept(resultset.getInt("type_cc_id"),connection));
		// Get plan identifiers
		ResultSet idResultSet = DatabaseUtil.runQuery(connection,"SELECT * from identifier where plan_id = ?", planId);
		while(idResultSet.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(idResultSet);
			plan.addIdentifier(identifier);
		}
		// Get plan coverage area
		ResultSet covResultset = DatabaseUtil.runQuery(connection,"SELECT * from resource_reference where plan_coverageArea_id = ?", planId);
		while(covResultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(covResultset,connection);
			plan.addCoverageArea(ref);
		}
		// Get plan networks
		ResultSet refResultSet = DatabaseUtil.runQuery(connection, "select * from resource_reference where plan_network_id =?", resultset.getInt("coverage_id"));
		while (refResultSet.next()) {
			Reference nw = ResourceFactory.getResourceReference(refResultSet,connection);
			plan.addNetwork(nw);
		}
		// Get plan general costs
		ResultSet costResultSet = DatabaseUtil.runQuery(connection, "select * from general_cost where plan_id =?", resultset.getInt("coverage_id"));
		while (costResultSet.next()) {
			plan.addGeneralCost(getGeneralCost(costResultSet,connection));
		}
		// Get plan specific costs
		ResultSet scResultSet = DatabaseUtil.runQuery(connection, "select * from specific_cost where plan_id =?", resultset.getInt("coverage_id"));
		while (scResultSet.next()) {
			plan.addSpecificCost(getSpecificCost(scResultSet, connection));
		}
		
		return plan;
	}


	/**
	 * Note that this is virtually the same method as ContactFactory.getContact except this method
	 * populates and returns a HAPI InsurancePlanContactComponent
	 *  
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public InsurancePlanContactComponent getInsurancePlanContact(ResultSet resultset, Connection connection) throws SQLException{
		InsurancePlanContactComponent con = new InsurancePlanContactComponent();
		con.setId(resultset.getString("contact_id"));
		con.setPurpose(ResourceFactory.getCodeableConcept(resultset.getInt("purpose_id"),connection));
		con.setName(ResourceFactory.getHumanName(resultset.getInt("name_id"),connection));
		if (connection != null) {
			// Gather the telecom contact points for this contact
			ResultSet tcresultset = DatabaseUtil.runQuery(connection, "SELECT * FROM telecom WHERE contact_id = ?", resultset.getInt("name_id"));
			while(tcresultset.next()) {
				VhDirTelecom tele = ContactFactory.getTelecom(tcresultset, connection);
				if (!tele.hasAvailableTime()) {
					// Add 9:00-4:30 any day, available time for this telecom contact point
					tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				}
				con.addTelecom(tele);
			}
			// Get the address for this contact
			con.setAddress(ResourceFactory.getAddress(tcresultset.getInt("address_id"),connection));
		}
		return con;
	}

	//----------------------------------------------------------------------------------------------------------------------
	// Protected and Private methods
	//----------------------------------------------------------------------------------------------------------------------

	protected static CoverageBenefitComponent getCoverageBenefit(ResultSet benResultSet, Connection connection) throws SQLException {
		CoverageBenefitComponent ben = new CoverageBenefitComponent();
		ben.setId(benResultSet.getString("benefit_id"));
		ben.setType(ResourceFactory.getCodeableConcept(benResultSet.getInt("type_cc_id"),connection));
		ben.setRequirement(benResultSet.getString("requirement"));
		ResultSet limitResultSet = DatabaseUtil.runQuery(connection,"select * from limit where benefit_id = ?", benResultSet.getInt("benefit_id"));
		while (limitResultSet.next()) {
			ben.addLimit(getCoverageLimit(limitResultSet,connection));
		}
		return ben;
	}
	
	protected static CoverageBenefitLimitComponent getCoverageLimit(ResultSet limitResultSet, Connection connection) throws SQLException {
		CoverageBenefitLimitComponent lim = new CoverageBenefitLimitComponent();
		lim.setId(limitResultSet.getString("limit_id"));
		Quantity q = new Quantity();
		String dbVal = limitResultSet.getString("value");
		// assume dbVal is of the form  "double units";
		if (dbVal != null && ! dbVal.isEmpty()) {
			String[] tokens = dbVal.split(" ");
			if (tokens.length > 0) {
				q.setValue(new Double(tokens[0]));
			}
			if (tokens.length > 1) {
				q.setUnit(tokens[1]);
			}
			lim.setValue(q);
		}
		lim.setCode(ResourceFactory.getCodeableConcept(limitResultSet.getInt("code_cc_id"), connection));
		return lim;
	}
	

	protected static InsurancePlanPlanGeneralCostComponent getGeneralCost(ResultSet costResultSet, Connection connection) throws SQLException {
		InsurancePlanPlanGeneralCostComponent cost = new InsurancePlanPlanGeneralCostComponent();
		cost.setId(costResultSet.getString("general_cost_id"));
		cost.setType(ResourceFactory.getCodeableConcept(costResultSet.getInt("type_cc_id"),connection));
		cost.setGroupSize(costResultSet.getInt("group_size"));
		cost.setComment(costResultSet.getString("comment"));
		Money q = new Money();
		Double dbVal = costResultSet.getDouble("cost");
		q.setCurrency("Dollars");
		q.setValue(dbVal);
		cost.setCost(q);
		return cost;
	}

	protected static InsurancePlanPlanSpecificCostComponent getSpecificCost(ResultSet scResultSet, Connection connection) throws SQLException {
		InsurancePlanPlanSpecificCostComponent cost = new InsurancePlanPlanSpecificCostComponent();
		cost.setId(scResultSet.getString("specific_cost_id"));
		cost.setCategory(ResourceFactory.getCodeableConcept(scResultSet.getInt("category_cc_id"),connection));
		ResultSet scBenResultSet = DatabaseUtil.runQuery(connection, "select * from specific_cost_benefit where specific_cost_id = ?", scResultSet.getInt("specific_cost_id"));
		while (scBenResultSet.next()) {
			PlanBenefitComponent ben = new PlanBenefitComponent();
			ben.setType(ResourceFactory.getCodeableConcept(scBenResultSet.getInt("type_cc_id"),connection));
			ResultSet benCostResultSet = DatabaseUtil.runQuery(connection,  "select * from specific_cost_benefit where specific_cost_id = ?", scBenResultSet.getInt("cost_benefit_id"));
			while (benCostResultSet.next()) {
				ben.addCost(getBenefitCost(benCostResultSet,connection));
			}
			cost.addBenefit(ben);
		}
		return cost;
	}

	protected static PlanBenefitCostComponent getBenefitCost(ResultSet benCostResultSet, Connection connection) throws SQLException {
		PlanBenefitCostComponent benCost = new PlanBenefitCostComponent();
		benCost.setId(benCostResultSet.getString("cost_benefit_individual_cost_id"));
		benCost.setType(ResourceFactory.getCodeableConcept(benCostResultSet.getInt("type_cc_id"),connection));
		benCost.setApplicability(ResourceFactory.getCodeableConcept(benCostResultSet.getInt("applicability_cc_id"),connection));
		Quantity q = new Quantity();
		Double dbVal = benCostResultSet.getDouble("value");
		q.setValue(dbVal);
		benCost.setValue(q);
		ResultSet qualResultSet = DatabaseUtil.runQuery(connection,"select * from fhir_codeable_concept where plan_cost_qualifier = ?",benCostResultSet.getInt("cost_benefit_individual_cost_id"));
		while (qualResultSet.next()) {
			benCost.addQualifiers(ResourceFactory.getCodeableConcept(qualResultSet));
		}
		return benCost;
	}
}
