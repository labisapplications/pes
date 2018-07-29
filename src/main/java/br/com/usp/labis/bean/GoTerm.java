package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.List;

public class GoTerm  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private GoAnnotation goAnnotation;
	private List<Protein> proteins;
	private List<GoTermCondition> conditions;
	private Double pvalueRatioAB;

	
	public GoAnnotation getGoAnnotation() {
		return goAnnotation;
	}
	public void setGoAnnotation(GoAnnotation goAnnotation) {
		this.goAnnotation = goAnnotation;
	}
	public List<Protein> getProteins() {
		return proteins;
	}
	public void setProteins(List<Protein> proteins) {
		this.proteins = proteins;
	}
	public List<GoTermCondition> getConditions() {
		return conditions;
	}
	public void setConditions(List<GoTermCondition> conditions) {
		this.conditions = conditions;
	}
	
	public Double getPvalueRatioAB() {
		return pvalueRatioAB;
	}
	public void setPvalueRatioAB(Double pvalueRatioAB) {
		this.pvalueRatioAB = pvalueRatioAB;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result + ((goAnnotation == null) ? 0 : goAnnotation.hashCode());
		result = prime * result + ((proteins == null) ? 0 : proteins.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoTerm other = (GoTerm) obj;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		} else if (!conditions.equals(other.conditions))
			return false;
		if (goAnnotation == null) {
			if (other.goAnnotation != null)
				return false;
		} else if (!goAnnotation.equals(other.goAnnotation))
			return false;
		if (proteins == null) {
			if (other.proteins != null)
				return false;
		} else if (!proteins.equals(other.proteins))
			return false;
		return true;
	}

	
	
}
