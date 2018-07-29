package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.List;

public class NullDistribution  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<Protein> proteins;
	private List<Double> means;
	private List<Double> weights; //protein weights
	private Double cv;
	private Double pvalue;
	private Double weight; //sum of weights
	
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
