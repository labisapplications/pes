package br.com.usp.labis.bean;

import java.util.List;

public class NullDistribution {
	
	List<Protein> proteins;
	List<Double> means;
	List<Double> weights; //protein weights
	Double cv;
	Double pvalue;
	Double weight; //sum of weights
	
	public List<Protein> getProteins() {
		return proteins;
	}
	public void setProteins(List<Protein> proteins) {
		this.proteins = proteins;
	}
	public List<Double> getMeans() {
		return means;
	}
	public void setMeans(List<Double> means) {
		this.means = means;
	}
	public Double getCv() {
		return cv;
	}
	public void setCv(Double cv) {
		this.cv = cv;
	}
	public Double getPvalue() {
		return pvalue;
	}
	public void setPvalue(Double pvalue) {
		this.pvalue = pvalue;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public List<Double> getWeights() {
		return weights;
	}
	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}
	

}
