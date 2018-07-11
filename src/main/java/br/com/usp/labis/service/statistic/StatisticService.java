package br.com.usp.labis.service.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.GoTermCondition;
import br.com.usp.labis.bean.NullDistribution;
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
	public Map<String, Double> getMaxMean(Map<String, List<Double>> conditionMeans,
			Map<String, Double> conditionMaxMean) {
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
	public Map<String, Double> getMaxCv(Map<String, List<Double>> conditionCvs, Map<String, Double> conditionMaxCv) {
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

	public void getGoTermProteinsCvForEachCondition(List<GoTerm> goTerms) {

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition condition : goTerm.getConditions()) {
				Double cv = calcCv(condition.getOriginalMeans());
				condition.setOriginalCv(cv);
			}
		}
	}

	public double calcCv(List<Double> means) {

		double[] array = DataUtil.getArray(means);

		double mean = DataUtil.getSumDoubleValues(array) / array.length;

		double stdeviation = this.calculateStandardDeviation(array);

		double cv = stdeviation / (mean + 0.0000000001);

		return cv;
	}

	public void getNullDistributions(Integer numberOfNullDistributions, Double toleranceFactor, List<GoTerm> goTerms,
			String conditionSelected, List<Protein> proteins, Boolean isCore) {

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				if (conditionSelected == null
						|| conditionSelected.equalsIgnoreCase(goTermCondition.getCondition().getName())) {

					List<NullDistribution> nullDistributions = new ArrayList<NullDistribution>();

					for (int i = 0; i < numberOfNullDistributions; i++) {

						NullDistribution nullDistribution = new NullDistribution();
						nullDistribution.setMeans(new ArrayList<Double>());
						nullDistribution.setWeights(new ArrayList<Double>());

						Integer proteinsSize = 0;

						try {
							proteinsSize = !isCore ? goTermCondition.getOriginalProteins().size()
									: goTermCondition.getCoreProteins().size();
						} catch (RuntimeException e) {
							System.out.println(" Erro =>" + e.getMessage());
						}

						List<Protein> proteinsRandomlySelected = DataUtil.getRandomlySelectedProteins(proteinsSize,
								proteins);

						if (proteinsRandomlySelected != null && !proteinsRandomlySelected.isEmpty()) {

							nullDistribution.setProteins(proteinsRandomlySelected);

							for (Protein prot : proteinsRandomlySelected) {
								for (Condition protCondition : prot.getConditions()) {
									if (protCondition.getName().equals(goTermCondition.getCondition().getName())) {
										nullDistribution.getMeans().add(protCondition.getMean());
										nullDistribution.getWeights().add(protCondition.getWeight());
									}
								}
							}
						}

						nullDistribution.setCv(this.calcCv(nullDistribution.getMeans()));

						Double cvToCompare = !isCore ? goTermCondition.getOriginalCv() : goTermCondition.getCoreCv();

						// calc the weight for random if the random cv calculate is between the range (1
						// - toleranceFactor) * originalCv and (1 + toleranceFactor) * originalCv
						try {
							if (nullDistribution.getCv() >= (1 - toleranceFactor) * cvToCompare
									&& nullDistribution.getCv() <= (1 + toleranceFactor) * cvToCompare) {
								Double weight = 0.00;
								for (Double weightProtein : nullDistribution.getWeights()) {
									weight += weightProtein;
								}
								nullDistribution.setWeight(weight);
							} else {
								nullDistribution.setWeight(0.00);
							}

							nullDistributions.add(nullDistribution);
						} catch (RuntimeException e) {
							System.out.println("Erro =>" + e.getMessage());
						}
					}

					if (!isCore) {
						goTermCondition.setNullDistributionsOriginal(nullDistributions);
					} else {
						goTermCondition.setNullDistributionsCore(nullDistributions);
					}
				}
			}
		}

	}

	public void getCoreProteins(List<GoTerm> goTerms, Map<String, Double> maxMean, Map<String, Double> maxCv,
			Double maxStatisticTest, Integer numberOfNullDistributions, Double toleranceFactor, Double pvalue,
			List<Protein> proteinsOriginal) {

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				if (goTermCondition.getPvalueOriginal() >= pvalue && goTermCondition.getOriginalProteins().size() > 1) {

					List<Protein> originalProteins = goTermCondition.getOriginalProteins();
					goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
					goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());

					for (Protein originaProt : originalProteins) {

						// get a list without the protein to see if pvalue will get better without it
						List<Protein> core = originalProteins.stream()
								.filter(i -> i.getProteinId() != originaProt.getProteinId())
								.collect(Collectors.toList());

						List<Double> means = new ArrayList<Double>();
						List<Double> weights = new ArrayList<Double>();

						for (Protein coreProt : core) {

							for (Condition coreProtCond : coreProt.getConditions()) {

								if (coreProtCond.getName().equals(goTermCondition.getCondition().getName())) {
									means.add(coreProtCond.getMean());
									weights.add(coreProtCond.getWeight());
								}
							}
						}

						Double cv = this.calcCv(means);

						Double weightSum = 0.00;
						for (Double weight : weights) {
							weightSum += weight;
						}
						
						goTermCondition.setCoreCv(cv);
						goTermCondition.setCoreWeight(weightSum);
						goTermCondition.setCoreProteins(core);
						
						List<GoTerm> goTermToGetNullDistributions = new ArrayList<GoTerm>();
						goTermToGetNullDistributions.add(goTerm);
						
						this.getNullDistributions(numberOfNullDistributions, toleranceFactor, goTermToGetNullDistributions,
								goTermCondition.getCondition().getName(), proteinsOriginal, true);

						this.compareNullDistributionPvalues(goTermToGetNullDistributions, pvalue, true);
						
						//goTerm = goTermToGetNullDistributions.get(0);

						if (goTermCondition.getPvalueCore() >= pvalue) {
							
							// add the protein again to the list
							core.add(originaProt);
							goTermCondition.setCoreProteins(core);
							break;
						}

						pvalue = goTermCondition.getPvalueCore();
						goTermCondition.setFinalPvalue(pvalue);
						goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
					}

				} else {

					goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
					goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());
					goTermCondition.setCoreProteins(goTerm.getProteins());
				}
			}
		}
	}

	public void compareNullDistributionPvalues(List<GoTerm> goTerms, Double pvalue, Boolean isCore) {

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				List<NullDistribution> nullDistributions = !isCore ? goTermCondition.getNullDistributionsOriginal()
						: goTermCondition.getNullDistributionsCore();

				if (nullDistributions != null && !nullDistributions.isEmpty()) {

					List<Double> filteredWeight = new ArrayList<Double>();

					Double numberOfDistributions = (double) nullDistributions.size();
					Double weightToCompare = !isCore ? goTermCondition.getOriginalWeight()
							: goTermCondition.getCoreWeight();

					for (NullDistribution nullDistribution : nullDistributions) {

						for (Double weight : nullDistribution.getWeights()) {
							if (weight > weightToCompare) {
								filteredWeight.add(weight);
							}
						}
					}

					Double pvalueCalculated = filteredWeight.size() / numberOfDistributions;

					if (!isCore) {
						goTermCondition.setPvalueOriginal(pvalueCalculated);
					} else {
						goTermCondition.setPvalueCore(pvalueCalculated);
					}
				}
			}
		}
	}

	public void calculateGoTermWeight(List<GoTerm> goTerms, List<Protein> proteins, String conditionSelected) {

		for (GoTerm goTerm : goTerms) {
			List<GoTermCondition> goTermConditions = new ArrayList<GoTermCondition>();

			Map<String, List<Double>> weightsMap = new HashMap<String, List<Double>>();
			Map<String, List<Double>> meansMap = new HashMap<String, List<Double>>();
			Map<String, Condition> conditions = new HashMap<String, Condition>();

			for (Protein proteinGoTerm : goTerm.getProteins()) {
				for (Protein protein : proteins) {
					if (protein.getProteinId().equalsIgnoreCase(proteinGoTerm.getProteinId())) {
						for (Condition condition : protein.getConditions()) {

							if (conditionSelected == null || conditionSelected.equalsIgnoreCase(condition.getName())) {

								// weights
								if (weightsMap.get(condition.getName()) == null) {
									weightsMap.put(condition.getName(), new ArrayList<Double>());
									conditions.put(condition.getName(), condition);
								}
								weightsMap.get(condition.getName()).add(condition.getWeight());

								// means
								if (meansMap.get(condition.getName()) == null) {
									meansMap.put(condition.getName(), new ArrayList<Double>());
								}
								meansMap.get(condition.getName()).add(condition.getMean());

							}
						}
					}
				}
			}

			Iterator itWeights = weightsMap.entrySet().iterator();

			while (itWeights.hasNext()) {
				Map.Entry pairs = (Map.Entry) itWeights.next();
				GoTermCondition goTermCondition = new GoTermCondition();
				goTermCondition.setCondition(conditions.get(pairs.getKey()));
				goTermCondition.setOriginalWeights((List<Double>) pairs.getValue());

				Double sumWeight = 0.00;
				for (Double weight : goTermCondition.getOriginalWeights()) {
					sumWeight = +weight;
				}
				goTermCondition.setOriginalWeight(sumWeight);
				goTermCondition.setOriginalMeans(meansMap.get(goTermCondition.getCondition().getName()));
				goTermCondition.setOriginalProteins(goTerm.getProteins());
				goTermConditions.add(goTermCondition);
			}
			goTerm.setConditions(goTermConditions);
		}

		// calc the coefficient of variation
		this.getGoTermProteinsCvForEachCondition(goTerms);
	}
}
