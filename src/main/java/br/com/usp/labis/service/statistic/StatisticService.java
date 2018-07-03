package br.com.usp.labis.service.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.useful.DataUtil;

@Component
public class StatisticService {

	/**
	 * ANOVA test for N independent samples.
	 * 
	 * @param conditions
	 *            protein conditions to be tested
	 * @return p-value calculated
	 */
	public double oneWayAnovaTest(List<Condition> conditions) {

		double pValue = 0.00;

		try {
			List<double[]> classesToTest = getReplicatesValuesToTest(conditions);

			pValue = TestUtils.oneWayAnovaPValue(classesToTest); // P-value
			System.out.println("Result ANOVA  P-value: " + pValue);

		} catch (Exception e) {
			System.out.println("Error ANOVA: " + e.getMessage() + e.getCause());
		}
		return pValue;
	}

	public List<double[]> getReplicatesValuesToTest(List<Condition> conditions) {
		List<double[]> classesToTest = new ArrayList<double[]>();
		for (Condition condition : conditions) {
			double[] replicatesValues = new double[condition.getReplicates().size()];
			Integer count = 0;
			for (Replicate replicate : condition.getReplicates()) {
				replicatesValues[count] = replicate.getValue();
				count++;
			}

			classesToTest.add(replicatesValues);
		}
		return classesToTest;
	}

	/**
	 * T Test for 2 independent samples considering 2 tails
	 * 
	 * @param conditions
	 *            protein conditions to be tested
	 * @return p-value calculated
	 */
	public double tTest(List<Condition> conditions) {
		double pValue = 0.00;
		try {
			List<double[]> classesToTest = getReplicatesValuesToTest(conditions);

			// test for 2 samples, 2 tail for independent samples
			pValue = TestUtils.tTest(classesToTest.get(0), classesToTest.get(1)); // P-value
			System.out.println("Result tTest  P-value: " + pValue);

		} catch (Exception e) {
			System.out.println("Error tTest: " + e.getMessage() + e.getCause());
		}
		return pValue;
	}

	/**
	 * Calculate the standard deviation
	 * 
	 * @param values
	 */
	public double calculateStandardDeviation(double[] values) {
		StandardDeviation sd = new StandardDeviation(false);
		return sd.evaluate(values);
	}

	/**
	 * Calculate for each protein Condition the Mean and the Coeficient of Variation
	 * 
	 * @param protein
	 */
	public void calculateProteinConditionMeanAndCv(Protein protein) {

		for (Condition condition : protein.getConditions()) {

			double[] values = DataUtil.getReplicatesValuesForCondition(condition);

			double mean = DataUtil.getSumDoubleValues(values) / condition.getReplicates().size();

			double stdeviation = this.calculateStandardDeviation(values);
			condition.setMean(mean);

			double cv = stdeviation / (mean + 0.0000000001); // add 0.0000000001 so it will never go on zero division
																// error
			condition.setCv(cv);

			System.out.println("condition mean " + condition.getMean());
			System.out.println("condition cv " + condition.getCv());

		}
	}

	/**
	 * Get the max mean for condition
	 * 
	 * @param conditionMeans
	 *            all condition means
	 */
	public Map<String, Double> getMaxMean(Map<String, List<Double>> conditionMeans) {
		Map<String, Double> conditionMaxMean = new HashMap<String, Double>();
		Iterator itMean = conditionMeans.entrySet().iterator();
		while (itMean.hasNext()) {
			Map.Entry pairs = (Map.Entry) itMean.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			Double maxMean = DataUtil.getMaxValueFromList((List<Double>) pairs.getValue());
			System.out.println("maxMean " + maxMean);
			conditionMaxMean.put((String) pairs.getKey(), maxMean);
		}

		return conditionMaxMean;
	}

	/**
	 * Get the max cv for condition
	 * 
	 * @param conditionCvs
	 *            all condition cvs
	 */
	public Map<String, Double> getMaxCv(Map<String, List<Double>> conditionCvs) {
		Map<String, Double> conditionMaxCv = new HashMap<String, Double>();
		Iterator itCv = conditionCvs.entrySet().iterator();
		while (itCv.hasNext()) {
			Map.Entry pairs = (Map.Entry) itCv.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			Double maxCv = DataUtil.getMaxValueFromList((List<Double>) pairs.getValue());
			System.out.println("maxCv " + maxCv);
			conditionMaxCv.put((String) pairs.getKey(), maxCv);
		}
		return conditionMaxCv;
	}
	
	/**
	 * Get the max statistic test for condition
	 * 
	 * @param statisticsTests
	 *            all tests 
	 */
	public  Double getMaxStatisticTest(List<Double> statisticsTests) {
		Double maxStat = DataUtil.getMaxValueFromList(statisticsTests);
		System.out.println("maxTest: " + maxStat);
		return maxStat;
	}

	/**
	 * Calculate the protein weight for each condition.
	 * @param List<Protein> proteins
	 * @param maxMean
	 * @param maxCv
	 * @param maxStatisticTest
	 **/
	public void calculateProteinWeightForEachCondition (List<Protein> proteins, Map<String, Double> maxMeans,
			Map<String, Double> maxCvs, Double maxStatisticTests) {
		
		for(Protein protein : proteins) {
			for (Condition condition : protein.getConditions()) {
				
				Double maxMeanCondition = maxMeans.get(condition.getName());
				Double maxCvCondition = maxCvs.get(condition.getName());
				Double maxStatisticTestCondition = maxStatisticTests;

				Double cvp = maxMeanCondition / maxCvCondition;
				Double test = maxMeanCondition / maxStatisticTestCondition;

				Double weight = ( condition.getMean() - 0.5 * cvp - 0.5 * test + maxMeanCondition - 1 ) / (2 * maxMeanCondition - 2 );
				
				condition.setWeight(weight);
				
				System.out.println("Weight: " + weight);
			}
		}
	}
}
