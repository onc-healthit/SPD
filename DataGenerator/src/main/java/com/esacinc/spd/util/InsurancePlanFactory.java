package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent;
import org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent;
import org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent;
import org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import com.esacinc.spd.model.VhDirIdentifier;

/**
 * This class has static public methods for return various data associated with Insurance Plans from the database.
 * Since there is so much structure and data around Insurance Plans, a separate factory class was indicated.
 * 
 * @author dandonahue
 *
 */
public class InsurancePlanFactory {

	
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

	public static CoverageBenefitComponent getCoverageBenefit(ResultSet benResultSet, Connection connection) throws SQLException {
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
	
	public static CoverageBenefitLimitComponent getCoverageLimit(ResultSet limitResultSet, Connection connection) throws SQLException {
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

	public static InsurancePlanPlanGeneralCostComponent getGeneralCost(ResultSet costResultSet, Connection connection) throws SQLException {
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

	public static InsurancePlanPlanSpecificCostComponent getSpecificCost(ResultSet scResultSet, Connection connection) throws SQLException {
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

	public static PlanBenefitCostComponent getBenefitCost(ResultSet benCostResultSet, Connection connection) throws SQLException {
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
