package br.com.usp.labis.useful;

import java.util.List;
import java.util.Map;

import br.com.usp.labis.bean.GoTerm;
import br.com.usp.labis.bean.Protein;

public class GoCoreParams {

	private List<GoTerm> goTerms;
	private Map<String, Double> maxMean;
	private Map<String, Double> maxCv;
	private Double maxStatisticTest;
	private Integer numberOfNullDistributions;
	private Double toleranceFactor;
	private Double pvalueDesired;
	private List<Protein> proteinsOriginal;

	public Map<String, Double> getMaxMean() {
		return maxMean;
	}

	public void setMaxMean(Map<String, Double> maxMean) {
		this.maxMean = maxMean;
	}

	public Map<String, Double> getMaxCv() {
		return maxCv;
	}

	public void setMaxCv(Map<String, Double> maxCv) {
		this.maxCv = maxCv;
	}

	public Double getMaxStatisticTest() {
		return maxStatisticTest;
	}

	public void setMaxStatisticTest(Double maxStatisticTest) {
		this.maxStatisticTest = maxStatisticTest;
	}

	public Integer getNumberOfNullDistributions() {
		return numberOfNullDistributions;
	}

	public void setNumberOfNullDistributions(Integer numberOfNullDistributions) {
		this.numberOfNullDistributions = numberOfNullDistributions;
	}

	public Double getToleranceFactor() {
		return toleranceFactor;
	}

	public void setToleranceFactor(Double toleranceFactor) {
		this.toleranceFactor = toleranceFactor;
	}

	public Double getPvalueDesired() {
		return pvalueDesired;
	}

	public void setPvalueDesired(Double pvalueDesired) {
		this.pvalueDesired = pvalueDesired;
	}

	public List<Protein> getProteinsOriginal() {
		return proteinsOriginal;
	}

	public void setProteinsOriginal(List<Protein> proteinsOriginal) {
		this.proteinsOriginal = proteinsOriginal;
	}

	public List<GoTerm> getGoTerms() {
		return goTerms;
	}

	public void setGoTerms(List<GoTerm> goTerms) {
		this.goTerms = goTerms;
	}

	
}
