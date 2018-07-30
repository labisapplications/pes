package br.com.usp.labis.service.statistic;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.usp.labis.bean.Condition;
import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.Protein;

@Component
public interface IStatisticService {

	/**
	 * ANOVA test for N independent samples.
	 * 
	 * @param conditions
	 *            protein conditions to be tested
	 * @return p-value calculated
	 */
	double oneWayAnovaTest(List<Condition> conditions);
	
	/**
	 * T Test for 2 independent samples considering 2 tails
	 * 
	 * @param conditions
	 *            protein conditions to be tested
	 * @return p-value calculated
	 */
	double tTest(List<Condition> conditions) ;

	/**
	 * Calculate the standard deviation
	 * 
	 * @param values
	 */
	double calculateStandardDeviation(double[] values) ;

	/**
	 * Calculate for each protein Condition the Mean and the Coeficient of Variation
	 * 
	 * @param protein
	 */
	void calculateProteinConditionMeanAndCv(Protein protein) ;

	/**
	 * Get the max mean for condition
	 * 
	 * @param conditionMeans
	 *            all condition means
	 */
	Map<String, Double> getMaxMean(Map<String, List<Double>> conditionMeans,
			Map<String, Double> conditionMaxMean) ;
	/**
	 * Get the max cv for condition
	 * 
	 * @param conditionCvs
	 *            all condition cvs
	 */
	Map<String, Double> getMaxCv(Map<String, List<Double>> conditionCvs, Map<String, Double> conditionMaxCv) ;

	/**
	 * Get the max statistic test for condition
	 * 
	 * @param statisticsTests
	 *            all tests
	 */
	Double getMaxStatisticTest(List<Double> statisticsTests);

	/**
	 * Calculate the protein weight for each condition.
	 * 
	 * @param List<Protein>
	 *            proteins
	 * @param maxMean
	 * @param maxCv
	 * @param maxStatisticTest
	 **/
	void calculateProteinWeightForEachCondition(List<Protein> proteins, Map<String, Double> maxMeans,
			Map<String, Double> maxCvs, Double maxStatisticTests) ;
	
	
	void getGoTermProteinsCvForEachCondition(List<GoTerm> goTerms) ;

	double calcCv(List<Double> means) ;

	void getNullDistributions(Integer numberOfNullDistributions, Double toleranceFactor, List<GoTerm> goTerms,
			String conditionSelected, List<Protein> proteins, Boolean isCore) ;

	void getCoreProteins(List<GoTerm> goTerms, Map<String, Double> maxMean, Map<String, Double> maxCv,
			Double maxStatisticTest, Integer numberOfNullDistributions, Double toleranceFactor, Double pvalueDesired,
			List<Protein> proteinsOriginal);

	void getCoreProteinsForGoTerm(GoTerm goTerm, Map<String, Double> maxMean, Map<String, Double> maxCv,
			Double maxStatisticTest, Integer numberOfNullDistributions, Double toleranceFactor, Double pvalueDesired,
			List<Protein> proteinsOriginal) ;

	void calcNullDistributionPvalues(List<GoTerm> goTerms, Double pvalue, Boolean isCore) ;

	void calculateGoTermWeight(List<GoTerm> goTerms, List<Protein> proteins, String conditionSelected) ;

	/*
	 * The FDR (Benjamini Hochberg) method: In this method, the P-values are first
	 * sorted and ranked. The smallest value gets rank 1, the second rank 2, and the
	 * largest gets rank N. Then, Benjamini-Hochberg critical value is (i/m)Q where
	 * i is the rank, m is the total number of tests, and Q is the false discovery
	 * rate you choose.
	 */
	void calcQValueUsingBenjaminiHochberg(List<GoTerm> goTerms, Double pvalue) ;
	
	void calcRatioBetweenConditions(List<GoTerm> goTerms, Integer numberOfDistributions) ;

	void calcRatioBetweenTwoConditions(List<GoTerm> goTerms, Integer numberOfDistributions) ;
}
