package br.com.usp.labis.service.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	public HashMap<String, HashMap<String, Double>> calculateGoTermWeight(
			Map<String, List<Protein>> goTermWithProteinsFiltered, List<Protein> proteins,
			Map<String, HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition,
			Map<String, HashMap<String, Double>> goTermProteinsCv) {

		HashMap<String, HashMap<String, Double>> goTermWeightPerCondition = new HashMap<String, HashMap<String, Double>>();

		Iterator it = goTermWithProteinsFiltered.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			List<Protein> proteinsGoTerm = (List<Protein>) pairs.getValue();

			for (Protein proteinGoTerm : proteinsGoTerm) {

				for (Protein protein : proteins) {
					if (protein.getProteinId().equalsIgnoreCase(proteinGoTerm.getProteinId())) {

						for (Condition condition : protein.getConditions()) {

							// weights
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

							// means
							if (goTermProteinsMeanForEachCondition.get((String) pairs.getKey()) == null) {
								goTermProteinsMeanForEachCondition.put((String) pairs.getKey(),
										new HashMap<String, List<Double>>());
							}
							if (goTermProteinsMeanForEachCondition.get((String) pairs.getKey())
									.get(condition.getName()) == null) {
								goTermProteinsMeanForEachCondition.get((String) pairs.getKey()).put(condition.getName(),
										new ArrayList<Double>());
							}
							goTermProteinsMeanForEachCondition.get((String) pairs.getKey()).get(condition.getName())
									.add(condition.getMean());

						}
					}
				}
			}
		}

		// coefficient of variation
		this.getGoTermProteinsCvForEachCondition(goTermProteinsMeanForEachCondition, goTermProteinsCv);

		DataUtil.printGoTermConditionWeights(goTermWeightPerCondition);
		System.out.println("GoTerm weight was calculated");

		return goTermWeightPerCondition;
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
					if (goTermProteinsMeanForEachCondition.get(goTerm).get(cond.getName()) == null) {
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
	public Map<String, HashMap<String, Double>> getGoTermProteinsCvForEachCondition(
			Map<String, HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition) {
		Map<String, HashMap<String, Double>> goTermProteinsCv = new HashMap<String, HashMap<String, Double>>();
		return getGoTermProteinsCvForEachCondition(goTermProteinsMeanForEachCondition, goTermProteinsCv);
	}

	/**
	 * Calculate the protein coeficient of variation of proteins for each go term in
	 * each condition
	 * 
	 * @param Map<String,
	 *            HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition
	 * @param Map<String,
	 *            HashMap<String, Double>> goTermProteinsCv
	 **/
	public Map<String, HashMap<String, Double>> getGoTermProteinsCvForEachCondition(
			Map<String, HashMap<String, List<Double>>> goTermProteinsMeanForEachCondition,
			Map<String, HashMap<String, Double>> goTermProteinsCv) {

		Iterator it = goTermProteinsMeanForEachCondition.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			String goTerm = (String) pairs.getKey();
			HashMap<String, List<Double>> conditionMeans = (HashMap<String, List<Double>>) pairs.getValue();

			Iterator it2 = conditionMeans.entrySet().iterator();

			while (it2.hasNext()) {

				Map.Entry pairs2 = (Map.Entry) it2.next();

				String condition = (String) pairs2.getKey();

				List<Double> means = (List<Double>) pairs2.getValue();

				double[] array = DataUtil.getArray(means);

				double mean = DataUtil.getSumDoubleValues(array) / array.length;

				double stdeviation = this.calculateStandardDeviation(array);

				double cv = stdeviation / (mean + 0.0000000001);

				if (goTermProteinsCv.get(goTerm) == null) {
					goTermProteinsCv.put(goTerm, new HashMap<String, Double>());
				}

				goTermProteinsCv.get(goTerm).put(condition, cv);

			}
		}
		return goTermProteinsCv;
	}

	public Map<String, Map<String, Map<String, Double>>> getNullDistributions(Integer numberOfNullDistributions,
			Double toleranceFactor, List<Protein> proteins, Map<String, HashMap<String, Double>> goTermProteinsCv,
			Map<String, List<Protein>> goTermWithProteinsFiltered) {

		Map<String, Map<String, Map<String, Double>>> goTermRandomProteinsWeight = new HashMap<String, Map<String, Map<String, Double>>>();
		Iterator it = goTermProteinsCv.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			String goTerm = (String) pairs.getKey();
			HashMap<String, Double> conditionCv = (HashMap<String, Double>) pairs.getValue();
			Iterator it2 = conditionCv.entrySet().iterator();

			Integer goProteinsNumber = goTermWithProteinsFiltered.get(goTerm).size();

			while (it2.hasNext()) {

				Map.Entry pairs2 = (Map.Entry) it2.next();
				String condition = (String) pairs2.getKey();
				Double originalCv = (Double) pairs2.getValue();

				goTermRandomProteinsWeight.put(goTerm, new HashMap<String, Map<String, Double>>());

				// calc the null distributions
				for (int nullDistribution = 0; nullDistribution < numberOfNullDistributions; nullDistribution++) {

					// get randomly proteins and calc the cv. After, compare this cv to goTermCv
					// previously calculated
					List<Protein> proteinsRandomlySelected = DataUtil.getRandomlySelectedProteins(goProteinsNumber,
							proteins);

					Map<String, List<Protein>> randomGoTermProteins = new HashMap<String, List<Protein>>();

					randomGoTermProteins.put(goTerm, proteinsRandomlySelected);

					Map<String, HashMap<String, List<Double>>> randomGoTermProteinsMeans = this
							.getGoTermProteinsMeanForEachCondition(randomGoTermProteins);

					Map<String, HashMap<String, Double>> randomGoTermProteinsCv = getGoTermProteinsCvForEachCondition(
							randomGoTermProteinsMeans);

					Double proteinRandomCv = randomGoTermProteinsCv.get(goTerm).get(condition);

					// calc the weight for random if the random cv calculate is between the range (1
					// - toleranceFactor) * originalCv and (1 + toleranceFactor) * originalCv
					if (proteinRandomCv >= (1 - toleranceFactor) * originalCv
							&& proteinRandomCv <= (1 + toleranceFactor) * originalCv) {
						Double weight = 0.00;
						for (Protein protein : proteinsRandomlySelected) {
							for (Condition cond : protein.getConditions()) {
								if (cond.getName().equalsIgnoreCase(condition)) {
									weight += cond.getWeight();
								}
							}
						}

						if (goTermRandomProteinsWeight.get(goTerm).get(condition) == null) {
							goTermRandomProteinsWeight.get(goTerm).put(condition, new HashMap<String, Double>());
						}
						goTermRandomProteinsWeight.get(goTerm).get(condition).put(nullDistribution + "", weight);
					}

				}
			}
		}
		return goTermRandomProteinsWeight;
	}

	public void getCoreProteins(Map<String, HashMap<String, Double>> goTermProteinsCv,
			HashMap<String, HashMap<String, Double>> goTermWeightPerCondition,
			Map<String, List<Protein>> goTermWithProteinsFiltered,
			Map<String, Map<String, Map<String, Double>>> goTermRandomProteinsWeight, Double pvalue,
			Map<String, Double> maxMean, Map<String, Double> maxCv, Double maxStatisticTest,
			Integer numberOfNullDistributions, Double toleranceFactor) {
		
		//GO_ID : Condition: NullDistribution : Weight
		Iterator it = goTermRandomProteinsWeight.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();

			String goTerm = (String) pairs.getKey();

			Map<String, Map<String, Double>> conditionNullDistributions = (Map<String, Map<String, Double>>) pairs
					.getValue();

			Iterator it2 = conditionNullDistributions.entrySet().iterator();

			while (it2.hasNext()) {

				Map.Entry pairs2 = (Map.Entry) it2.next();

				String condition = (String) pairs2.getKey();

				Map<String, Double> nullDistributionWeights = (Map<String, Double>) pairs2.getValue();
				
				Iterator it3 = nullDistributionWeights.entrySet().iterator();
				List<Double> weights = new ArrayList<Double>();
				
				while (it3.hasNext()) {
					Map.Entry pairs3 = (Map.Entry) it3.next();
					weights.add((Double) pairs3.getValue());
				}

				Double numberOfDistributions = (double) nullDistributionWeights.keySet().size();

				Double originalWeight = 0.00;
				if (goTermWeightPerCondition.get(condition) != null
						&& goTermWeightPerCondition.get(condition).get(goTerm) != null) {
					originalWeight = goTermWeightPerCondition.get(condition).get(goTerm);
				}

				List<Double> filteredWeight = new ArrayList<Double>();

				for (Double weight : weights) {
					if (weight > originalWeight)
						filteredWeight.add(weight);
				}

				Double pvalueCalculated = filteredWeight.size() / numberOfDistributions;

				if (pvalueCalculated <= pvalue) {
					// get the core proteins for the go term
					List<Protein> original = goTermWithProteinsFiltered.get(goTerm);
					for (Protein prot : original) {
						List<Protein> core = original.stream().filter(i -> i.getProteinId() != prot.getProteinId())
								.collect(Collectors.toList());
						Double newPValue = 0.00;

						// calculate the new weight, cvs and means
						Map<String, HashMap<String, List<Double>>> goTermCoreMeanForEachCondition = new HashMap<String, HashMap<String, List<Double>>>();
						Map<String, HashMap<String, Double>> goTermCoreCv = new HashMap<String, HashMap<String, Double>>();

						HashMap<String, HashMap<String, Double>> coreWeights = calculateGoTermWeight(
								goTermWithProteinsFiltered, core, goTermCoreMeanForEachCondition, goTermCoreCv);

						// calculate the null distributions
						Map<String, Map<String, Map<String, Double>>> goTermRandomCoreWeight = getNullDistributions(
								numberOfNullDistributions, toleranceFactor, core, goTermProteinsCv,
								goTermWithProteinsFiltered);

					}

				}
			}

		}
	}

}
