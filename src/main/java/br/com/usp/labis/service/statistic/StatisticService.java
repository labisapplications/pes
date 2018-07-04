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
		StandardDeviation sd = new StandardDeviation(true);
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

			System.out.println("condition stdv " + stdeviation);
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
	public Double getMaxStatisticTest(List<Double> statisticsTests) {
		Double maxStat = DataUtil.getMaxValueFromList(statisticsTests);
		System.out.println("maxTest: " + maxStat);
		return maxStat;
	}

	/**
	 * Calculate the protein weight for each condition.
	 * 
	 * @param List<Protein>
	 *            proteins
	 * @param maxMean
	 * @param maxCv
	 * @param maxStatisticTest
	 **/
	public void calculateProteinWeightForEachCondition(List<Protein> proteins, Map<String, Double> maxMeans,
			Map<String, Double> maxCvs, Double maxStatisticTests) {
		System.out.println("maxStatisticTests: " + maxStatisticTests);

		for (Protein protein : proteins) {
			for (Condition condition : protein.getConditions()) {

				Double maxMeanCondition = maxMeans.get(condition.getName());
				Double maxCvCondition = maxCvs.get(condition.getName());
				Double maxStatisticTestCondition = maxStatisticTests;

				Double cvp = maxMeanCondition / maxCvCondition;
				Double test = maxMeanCondition / maxStatisticTestCondition;

				Double weight = (condition.getMean() - 0.5 * cvp - 0.5 * test + maxMeanCondition - 1)
						/ (2 * maxMeanCondition - 2);

				condition.setWeight(weight);

				System.out.println(protein.getProteinId() + " - Weight: " + weight);
			}
		}
	}

	/**
	 * Calculate the weight of the goId for each condition (condition: go_id:
	 * weight)
	 * 
	 * @param Map<String,
	 *            List<Protein>> goTermWithProteinsFiltered
	 * @param proteins
	 **/
	public void calculateGoTermWeight(Map<String, List<Protein>> goTermWithProteinsFiltered, List<Protein> proteins,
			HashMap<String, HashMap<String, Double>> goTermWeightPerCondition) {

		goTermWeightPerCondition = new HashMap<String, HashMap<String, Double>>();

		Iterator it = goTermWithProteinsFiltered.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			List<Protein> proteinsGoTerm = (List<Protein>) pairs.getValue();

			for (Protein proteinGoTerm : proteinsGoTerm) {

				for (Protein protein : proteins) {
					if (protein.getProteinId().equalsIgnoreCase(proteinGoTerm.getProteinId())) {

						for (Condition condition : protein.getConditions()) {

							if (goTermWeightPerCondition.get(condition.getName()) == null) {
								goTermWeightPerCondition.put(condition.getName(), new HashMap<String, Double>());
							}

							if (goTermWeightPerCondition.get(condition.getName())
									.get((String) pairs.getKey()) == null) {
								goTermWeightPerCondition.get(condition.getName()).put((String) pairs.getKey(),
										condition.getWeight());
							} else {
								goTermWeightPerCondition.get(condition.getName()).put((String) pairs.getKey(),
										goTermWeightPerCondition.get(condition.getName()).get((String) pairs.getKey())
												+ condition.getWeight());
							}
						}
					}
				}
			}
		}

		DataUtil.printGoTermConditionWeights(goTermWeightPerCondition);
		System.out.println("GoTerm weight was calculated");
	}

	/**
	 * Get the for each go term the protein means in each condition
	 * 
	 * @param Map<String,
	 *            List<Protein>> goTermWithProteinsFiltered
	 **/
	public Map<String, HashMap<String, List<Double>>> getGoTermProteinsMeanForEachCondition(
			Map<String, List<Protein>> goTermWithProteinsFiltered) {

		Map<String, HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition = new HashMap<String, HashMap<String, List<Double>>>();

		Iterator it = goTermWithProteinsFiltered.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			String goTerm = (String) pairs.getKey();
			List<Protein> proteinsGoTerm = (List<Protein>) pairs.getValue();

			for (int i = 0; i < proteinsGoTerm.size(); i++) {
				List<Condition> conditions = proteinsGoTerm.get(i).getConditions();
				for (Condition cond : conditions) {
					if (goTermProteinsMeanForEachCondition.get(goTerm) == null) {
						goTermProteinsMeanForEachCondition.put(goTerm, new HashMap<String, List<Double>>());
					}
					if (goTermProteinsMeanForEachCondition.get(goTerm).get(cond) == null) {
						goTermProteinsMeanForEachCondition.get(goTerm).put(cond.getName(), new ArrayList<Double>());
					}
					goTermProteinsMeanForEachCondition.get(goTerm).get(cond.getName()).add(cond.getMean());
				}
			}

		}
		return goTermProteinsMeanForEachCondition;
	}

	/**
	 * Calculate the protein coeficient of variation of proteins for each go term in
	 * each condition
	 * 
	 * @param Map<String,
	 *            HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition
	 **/
	public Map<String, HashMap<String, List<Double>>> getGoTermProteinsCvForEachCondition(Map<String, HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition) {
		
		Map<String, HashMap<String, List<Double>>> goTermProteinsCv = new HashMap<String, HashMap<String, List<Double>>>();

		Iterator it = goTermProteinsMeanForEachCondition.entrySet().iterator();
		while (it.hasNext()) {
			
			Map.Entry pairs = (Map.Entry) it.next();
			String goTerm = (String) pairs.getKey();
			HashMap<String, List<Double>> values = (HashMap<String, List<Double>>) pairs.getValue();
			
			Iterator it2 = values.entrySet().iterator();
			
			while (it2.hasNext()) {

				Map.Entry pairs2 = (Map.Entry) it2.next();
				
				String condition = (String) pairs2.getKey();
				
				List<Double> values2 = (List<Double>) pairs2.getValue();

				double[] array = DataUtil.getArray(values2);

				double mean = DataUtil.getSumDoubleValues(array) / array.length;

				double stdeviation = this.calculateStandardDeviation(array);

				double cv = stdeviation / (mean + 0.0000000001);
				
				if (goTermProteinsCv.get(goTerm) == null) {
					goTermProteinsCv.put(goTerm, new HashMap<String, List<Double>>());
				}
				if (goTermProteinsCv.get(goTerm).get(condition) == null) {
					goTermProteinsCv.get(goTerm).put(condition, new ArrayList<Double>());
				}
				goTermProteinsCv.get(goTerm).get(condition).add(cv);

			}
		}
		return goTermProteinsCv;
	}

}
