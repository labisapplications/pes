package br.com.usp.labis.service.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.GoTermCondition;
import br.com.usp.labis.bean.NullDistribution;
import br.com.usp.labis.bean.Protein;
import br.com.usp.labis.bean.Replicate;
import br.com.usp.labis.exception.CustomException;
import br.com.usp.labis.useful.DataUtil;

@Component
public class StatisticService {

	@Autowired
	private MessageSource messageSource;

	private final Double DEAL_WITH_ZERO_DEVISION = 0.000000000001;

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
			pValue = Double.isNaN(pValue) ? 0.00 : pValue;
			System.out.println("Result ANOVA  P-value: " + pValue);

		} catch (Exception e) {
			System.out.println("Error ANOVA: " + e.getMessage() + e.getCause());
		}
		return DataUtil.round(pValue, 2);
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
			pValue = Double.isNaN(pValue) ? 0.00 : pValue;
			System.out.println("Result tTest  P-value: " + pValue);

		} catch (Exception e) {
			System.out.println("Error tTest: " + e.getMessage() + e.getCause());
		}
		return DataUtil.round(pValue, 2);
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

			if (values == null || values.length == 0) {
				throw new CustomException(messageSource.getMessage("messages.errorStatisticTest",
						new Object[] { condition.getName() }, Locale.US));
			}

			double mean = DataUtil.getSumDoubleValues(values) / condition.getReplicates().size();

			double stdeviation = this.calculateStandardDeviation(values);

			condition.setMean(DataUtil.round(mean, 2));

			double cv = stdeviation / (mean + DEAL_WITH_ZERO_DEVISION); // add DEAL_WITH_ZERO_DEVISION so it will never
																		// go on zero division
			// error
			condition.setCv(DataUtil.round(cv, 2));

			condition.setSd(DataUtil.round(stdeviation, 2));

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
		System.out.println("maxMeans: " + maxMeans);
		System.out.println("maxCvs: " + maxCvs);

		for (Protein protein : proteins) {

			for (Condition condition : protein.getConditions()) {

				Double maxMeanCondition = maxMeans.get(condition.getName()) == null ? 0.00
						: maxMeans.get(condition.getName());
				Double maxCvCondition = maxCvs.get(condition.getName()) == null ? 0.00
						: maxCvs.get(condition.getName());
				// Double maxStatisticTest = maxStatisticTests == null ? 0.00 :
				// maxStatisticTests;

				/*
				 * //Double cvp = maxMeanCondition / (maxCvCondition + DEAL_WITH_ZERO_DEVISION);
				 * Double test = maxMeanCondition / (maxStatisticTest +
				 * DEAL_WITH_ZERO_DEVISION);
				 * 
				 * Double weight = (condition.getMean() - (0.5 * cvp) - (0.5 * test) +
				 * maxMeanCondition - 1) / (2 * maxMeanCondition - 2);
				 * 
				 * weight = weight == null || Double.isInfinite(weight) || weight < 0.000 ? 0.00
				 * : weight;
				 */

				Double cvProtCondition = condition.getMean() / (condition.getSd() + DEAL_WITH_ZERO_DEVISION);

				Double meanProtCondition = condition.getMean();

				Double cvProtConditionAdjusted = (cvProtCondition / (maxCvCondition + DEAL_WITH_ZERO_DEVISION))
						* maxMeanCondition;

				Double weight = (meanProtCondition - cvProtConditionAdjusted + maxMeanCondition - 1)
						/ ((2 * maxMeanCondition - 2) + DEAL_WITH_ZERO_DEVISION);

				weight = (weight == null || Double.isInfinite(weight) || weight < 0.00 ? 0.00 : weight);

				condition.setWeight(DataUtil.round(weight, 2));

				System.out.println(protein.getProteinId() + " - Weight: " + condition.getWeight());
			}
		}
	}

	public void getGoTermProteinsCvForEachCondition(List<GoTerm> goTerms) {

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition condition : goTerm.getConditions()) {
				
				Double cv = calcCv(condition.getOriginalMeans());

				if (cv == null) {
					throw new CustomException(messageSource.getMessage("messages.erroToCalcGoTermConditionCv",
							new Object[] {
									goTerm.getGoAnnotation().getId() + " - " + condition.getCondition().getName() },
							Locale.US));
				}

				condition.setOriginalCv(cv);
			}
		}
	}

	public double calcCv(List<Double> means) {

		double[] array = DataUtil.getArray(means);

		double mean = DataUtil.getSumDoubleValues(array) / (array.length + 0.000000001);

		double stdeviation = this.calculateStandardDeviation(array);

		double cv = stdeviation / (mean + DEAL_WITH_ZERO_DEVISION);

		return DataUtil.round(cv, 2);
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

						proteinsSize = !isCore ? goTermCondition.getOriginalProteins().size()
								: goTermCondition.getCoreProteins().size();

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
						if (nullDistribution.getCv() >= (1 - DataUtil.round(toleranceFactor, 2)) * cvToCompare
								&& nullDistribution.getCv() <= (1 + DataUtil.round(toleranceFactor, 2) * cvToCompare)) {
							
							Double weight = 0.00;
							
							for (Double weightProtein : nullDistribution.getWeights()) {
								weight += weightProtein;
							}
							
							nullDistribution.setWeight(DataUtil.round(weight, 2));
							
						} else {
							nullDistribution.setWeight(goTermCondition.getOriginalWeight());
						}

						nullDistributions.add(nullDistribution);
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
			Double maxStatisticTest, Integer numberOfNullDistributions, Double toleranceFactor, Double pvalueDesired,
			List<Protein> proteinsOriginal) {

		pvalueDesired = DataUtil.round(pvalueDesired, 2);
		
		toleranceFactor = DataUtil.round(toleranceFactor, 2);

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				// try to improve the pvalue if the calculated pvalue is higher than the desired
				// and exists more than one protein in the protein set
				if (goTermCondition.getPvalueOriginal() > pvalueDesired
						&& goTermCondition.getOriginalProteins().size() > 1 ) {

					List<Protein> originalProteins = goTermCondition.getOriginalProteins();

					Double pvalueToCompare = goTermCondition.getPvalueOriginal();

					// order the proteins by weight in ascending order
					List<Protein> originalProteinsOrdered = DataUtil.orderProteinConditionWeightAsc(originalProteins,
							goTermCondition.getCondition());

					// first: consider that the final pvalue and final weight are like the original
					// ones
					goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
					
					goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());

					// for each protein
					for (Protein originaProt : originalProteinsOrdered) {

						// get a list without the protein to see if pvalue will get better without it
						List<Protein> core = originalProteinsOrdered.stream()
								.filter(i -> i.getProteinId() != originaProt.getProteinId())
								.collect(Collectors.toList());

						List<Double> means = new ArrayList<Double>();
						
						List<Double> weights = new ArrayList<Double>();

						// calculate the mean, weight and cv without the protein
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
						
						goTermCondition.setCoreWeight(DataUtil.round(weightSum, 2));
						
						goTermCondition.setCoreProteins(core);

						// calc the null distribution and the core pvalue to this core protein set
						List<GoTerm> goTermToGetNullDistributions = new ArrayList<GoTerm>();
						
						goTermToGetNullDistributions.add(goTerm);

						this.getNullDistributions(numberOfNullDistributions, toleranceFactor,
								goTermToGetNullDistributions, goTermCondition.getCondition().getName(),
								proteinsOriginal, true);

						this.calcNullDistributionPvalues(goTermToGetNullDistributions, pvalueDesired, true);

						/*
						 * pvalue = goTermCondition.getPvalueOriginal();
						 * 
						 * //if the pvalue did not improve, stop if (goTermCondition.getPvalueCore() >=
						 * pvalue) { goTermCondition.setCoreProteins(core); break; }
						 * 
						 * //continue until get a good pvalue or until exists just one protein in the
						 * set pvalue = goTermCondition.getPvalueCore();
						 * goTermCondition.setFinalPvalue(pvalue);
						 * goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
						 * originalProteinsOrdered = core;
						 */

						// if the pvalue was improved, stop because the goal was achieved!
						if (goTermCondition.getPvalueCore().compareTo(pvalueToCompare) < 0
								&& (goTermCondition.getPvalueCore().compareTo(pvalueDesired) == 0
										|| goTermCondition.getPvalueCore().compareTo(pvalueDesired) < 0)) {
							
							goTermCondition.setCoreProteins(core);
							
							goTermCondition.setFinalPvalue(goTermCondition.getPvalueCore());
							
							goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
							
							break;
						}

						// else continue until get a good core pvalue or until exists just one protein
						// in the set
						pvalueToCompare = goTermCondition.getPvalueCore();
						originalProteinsOrdered = core;

						if (originalProteinsOrdered.size() == 1) {
							
							// if after all the pvalue did not improve, then consider the original values
							if (goTermCondition.getPvalueCore().compareTo(goTermCondition.getPvalueOriginal()) == 0
									|| goTermCondition.getPvalueCore()
											.compareTo(goTermCondition.getPvalueOriginal()) > 0) {
								
								goTermCondition.setCoreProteins(goTermCondition.getOriginalProteins());
								
								goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
								
								goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());
								
							} else {
								
								goTermCondition.setCoreProteins(core);
								
								goTermCondition.setFinalPvalue(goTermCondition.getPvalueCore());
								
								goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
							}
							break;
						}
					}

				} else {

					goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
					
					goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());
					
					goTermCondition.setCoreProteins(goTermCondition.getOriginalProteins());
				}
			}
		}
	}
	
	public void getCoreProteinsForGoTerm(GoTerm goTerm, Map<String, Double> maxMean, Map<String, Double> maxCv,
			Double maxStatisticTest, Integer numberOfNullDistributions, Double toleranceFactor, Double pvalueDesired,
			List<Protein> proteinsOriginal) {

		pvalueDesired = DataUtil.round(pvalueDesired, 2);
		
		toleranceFactor = DataUtil.round(toleranceFactor, 2);

		System.out.println("getCoreProteinsForGoTerm : " + goTerm.getGoAnnotation().getGoId());
		
		//for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				// try to improve the pvalue if the calculated pvalue is higher than the desired
				// and exists more than one protein in the protein set
				if (goTermCondition.getPvalueOriginal() > pvalueDesired
						&& goTermCondition.getOriginalProteins().size() > 1 ) {

					List<Protein> originalProteins = goTermCondition.getOriginalProteins();

					Double pvalueToCompare = goTermCondition.getPvalueOriginal();

					// order the proteins by weight in ascending order
					List<Protein> originalProteinsOrdered = DataUtil.orderProteinConditionWeightAsc(originalProteins,
							goTermCondition.getCondition());

					// first: consider that the final pvalue and final weight are like the original
					// ones
					goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
					
					goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());

					// for each protein
					for (Protein originaProt : originalProteinsOrdered) {

						// get a list without the protein to see if pvalue will get better without it
						List<Protein> core = originalProteinsOrdered.stream()
								.filter(i -> i.getProteinId() != originaProt.getProteinId())
								.collect(Collectors.toList());

						List<Double> means = new ArrayList<Double>();
						
						List<Double> weights = new ArrayList<Double>();

						// calculate the mean, weight and cv without the protein
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
						
						goTermCondition.setCoreWeight(DataUtil.round(weightSum, 2));
						
						goTermCondition.setCoreProteins(core);

						// calc the null distribution and the core pvalue to this core protein set
						List<GoTerm> goTermToGetNullDistributions = new ArrayList<GoTerm>();
						
						goTermToGetNullDistributions.add(goTerm);

						this.getNullDistributions(numberOfNullDistributions, toleranceFactor,
								goTermToGetNullDistributions, goTermCondition.getCondition().getName(),
								proteinsOriginal, true);

						this.calcNullDistributionPvalues(goTermToGetNullDistributions, pvalueDesired, true);

						/*
						 * pvalue = goTermCondition.getPvalueOriginal();
						 * 
						 * //if the pvalue did not improve, stop if (goTermCondition.getPvalueCore() >=
						 * pvalue) { goTermCondition.setCoreProteins(core); break; }
						 * 
						 * //continue until get a good pvalue or until exists just one protein in the
						 * set pvalue = goTermCondition.getPvalueCore();
						 * goTermCondition.setFinalPvalue(pvalue);
						 * goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
						 * originalProteinsOrdered = core;
						 */

						// if the pvalue was improved, stop because the goal was achieved!
						if (goTermCondition.getPvalueCore().compareTo(pvalueToCompare) < 0
								&& (goTermCondition.getPvalueCore().compareTo(pvalueDesired) == 0
										|| goTermCondition.getPvalueCore().compareTo(pvalueDesired) < 0)) {
							
							goTermCondition.setCoreProteins(core);
							
							goTermCondition.setFinalPvalue(goTermCondition.getPvalueCore());
							
							goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
							
							break;
						}

						// else continue until get a good core pvalue or until exists just one protein
						// in the set
						pvalueToCompare = goTermCondition.getPvalueCore();
						originalProteinsOrdered = core;

						if (originalProteinsOrdered.size() == 1) {
							
							// if after all the pvalue did not improve, then consider the original values
							if (goTermCondition.getPvalueCore().compareTo(goTermCondition.getPvalueOriginal()) == 0
									|| goTermCondition.getPvalueCore()
											.compareTo(goTermCondition.getPvalueOriginal()) > 0) {
								
								goTermCondition.setCoreProteins(goTermCondition.getOriginalProteins());
								
								goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
								
								goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());
								
							} else {
								
								goTermCondition.setCoreProteins(core);
								
								goTermCondition.setFinalPvalue(goTermCondition.getPvalueCore());
								
								goTermCondition.setFinalWeight(goTermCondition.getCoreWeight());
							}
							break;
						}
					}

				} else {

					goTermCondition.setFinalPvalue(goTermCondition.getPvalueOriginal());
					
					goTermCondition.setFinalWeight(goTermCondition.getOriginalWeight());
					
					goTermCondition.setCoreProteins(goTermCondition.getOriginalProteins());
				}
			}
		//}
	}

	public void calcNullDistributionPvalues(List<GoTerm> goTerms, Double pvalue, Boolean isCore) {

		for (GoTerm goTerm : goTerms) {

			for (GoTermCondition goTermCondition : goTerm.getConditions()) {

				List<NullDistribution> nullDistributions = !isCore ? goTermCondition.getNullDistributionsOriginal()
						: goTermCondition.getNullDistributionsCore();

				/*if (nullDistributions == null) {
					throw new CustomException(messageSource.getMessage("messages.noNullDistributions",
							new Object[] { isCore + ": " + goTerm.getGoAnnotation().getId() + " - "
									+ goTermCondition.getCondition().getName() }, Locale.US));
				}*/

				if (nullDistributions != null && !nullDistributions.isEmpty()) {

					List<Double> filteredWeight = new ArrayList<Double>();

					Double numberOfDistributions = (double) nullDistributions.size();
					
					Double weightToCompare = !isCore ? goTermCondition.getOriginalWeight()
							: goTermCondition.getCoreWeight();

					// select the null distribution with higher or equal the the original weight
					for (NullDistribution nullDistribution : nullDistributions) {
						
						if (nullDistribution.getWeight() >= weightToCompare) {
							
							filteredWeight.add(nullDistribution.getWeight());
						}
					}

					Double pvalueCalculated = filteredWeight.size() / (numberOfDistributions + 0.000000001);
					
					pvalueCalculated = DataUtil.round(pvalueCalculated, 2);

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

				for (Condition condition : proteinGoTerm.getConditions()) {

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

			Iterator itWeights = weightsMap.entrySet().iterator();
			try {
				while (itWeights.hasNext()) {
					
					Map.Entry pairs = (Map.Entry) itWeights.next();
					
					GoTermCondition goTermCondition = new GoTermCondition();
					
					goTermCondition.setCondition(conditions.get(pairs.getKey()));
					
					goTermCondition.setOriginalWeights((List<Double>) pairs.getValue());

					Double sumWeight = 0.00;
					
					for (Double weight : goTermCondition.getOriginalWeights()) {
						
						sumWeight += weight;
					}
					
					goTermCondition.setOriginalWeight(DataUtil.round(sumWeight, 2));
					
					goTermCondition.setOriginalMeans(meansMap.get(goTermCondition.getCondition().getName()));
					
					goTermCondition.setOriginalProteins(goTerm.getProteins());
					
					goTermConditions.add(goTermCondition);
				}
				
			} catch (RuntimeException e) {
				throw new CustomException(messageSource.getMessage("messages.errorToSetGoTermCondition",
						new Object[] { e.getMessage() }, Locale.US));
			}
			
			goTerm.setConditions(goTermConditions);
		}

		// calc the coefficient of variation
		this.getGoTermProteinsCvForEachCondition(goTerms);
	}

	/*
	 * The FDR (Benjamini Hochberg) method: In this method, the P-values are first
	 * sorted and ranked. The smallest value gets rank 1, the second rank 2, and the
	 * largest gets rank N. Then, Benjamini-Hochberg critical value is (i/m)Q where
	 * i is the rank, m is the total number of tests, and Q is the false discovery
	 * rate you choose.
	 */
	public void calcQValueUsingBenjaminiHochberg(List<GoTerm> goTerms, Double pvalue) {
		List<Condition> conditions = new ArrayList<Condition>();

		try {
			// get the conditions to process
			for (GoTermCondition goTermCondition : goTerms.get(0).getConditions()) {
				conditions.add(goTermCondition.getCondition());
			}

			for (Condition condition : conditions) {

				ArrayList<GoTermCondition> goTermConditionPvalueSorted = DataUtil
						.orderGoTermConditionByPvalueAsc(goTerms, condition);

				// Integer quantity = goTermConditionPvalueSorted.size();

				// get the rank for each gotermcondition. Same pvalues have the same rank
				// position.
				Double lastFinalPValue = goTermConditionPvalueSorted.get(0).getFinalPvalue() == null ? 0.00
						: goTermConditionPvalueSorted.get(0).getFinalPvalue();
				Double rank = 1.00;

				for (int i = 0; i < goTermConditionPvalueSorted.size(); i++) {
					Double finalPValue = goTermConditionPvalueSorted.get(i).getFinalPvalue() == null ? 0.00
							: goTermConditionPvalueSorted.get(i).getFinalPvalue();
					if (finalPValue.doubleValue() != lastFinalPValue.doubleValue()) {
						lastFinalPValue = finalPValue;
						rank += 1.00;
					}
					goTermConditionPvalueSorted.get(i).setRank(rank.intValue());
				}

				// calc the q value for each element in the list
				for (int i = 0; i < goTermConditionPvalueSorted.size(); i++) {
					Double qvalue = goTermConditionPvalueSorted.get(i).getFinalPvalue()
							* (goTermConditionPvalueSorted.get(i).getRank() / rank);
					goTermConditionPvalueSorted.get(i).setQvalue(DataUtil.round(qvalue, 2));
				}
			}
		} catch (RuntimeException e) {
			System.out.println("Error to calc FDR: " + e.getMessage() + e.getCause());
			throw new CustomException(messageSource.getMessage("messages.errorFDR",
					new Object[] { e.getMessage() + " - " + e.getCause() }, Locale.US));
		}

	}
}
