package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.List;

public class GoTermCondition implements Serializable {

	private static final long serialVersionUID = 1L;

	private Condition condition;
	private List<Double> originalWeights;
	private Double originalWeight;
	private List<Double> originalMeans;
	private Double originalCv;
	private Double coreCv;
	private Double coreWeight;
	private Double pvalueOriginal;
	private Double pvalueCore;
	private Double finalPvalue;
	private Double finalWeight;
	private Double pvalueRecalculated;
	private Double maxStatisticTest;
	private List<Protein> originalProteins;
	private List<Protein> coreProteins;
	private List<NullDistribution> nullDistributionsOriginal;
	private List<NullDistribution> nullDistributionsCore;
	private Double qvalue;
	private Integer rank;

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<Double> getOriginalWeights() {
		return originalWeights;
	}

	public void setOriginalWeights(List<Double> originalWeights) {
		this.originalWeights = originalWeights;
	}

	public List<Double> getOriginalMeans() {
		return originalMeans;
	}

	public void setOriginalMeans(List<Double> originalMeans) {
		this.originalMeans = originalMeans;
	}

	public Double getPvalueOriginal() {
		return pvalueOriginal;
	}

	public void setPvalueOriginal(Double pvalueOriginal) {
		this.pvalueOriginal = pvalueOriginal;
	}

	public Double getPvalueRecalculated() {
		return pvalueRecalculated;
	}

	public void setPvalueRecalculated(Double pvalueRecalculated) {
		this.pvalueRecalculated = pvalueRecalculated;
	}

	public Double getMaxStatisticTest() {
		return maxStatisticTest;
	}

	public void setMaxStatisticTest(Double maxStatisticTest) {
		this.maxStatisticTest = maxStatisticTest;
	}

	public List<Protein> getOriginalProteins() {
		return originalProteins;
	}

	public void setOriginalProteins(List<Protein> originalProteins) {
		this.originalProteins = originalProteins;
	}

	public List<Protein> getCoreProteins() {
		return coreProteins;
	}

	public void setCoreProteins(List<Protein> coreProteins) {
		this.coreProteins = coreProteins;
	}

	public Double getOriginalCv() {
		return originalCv;
	}

	public void setOriginalCv(Double originalCv) {
		this.originalCv = originalCv;
	}

	public Double getOriginalWeight() {
		return originalWeight;
	}

	public void setOriginalWeight(Double originalWeight) {
		this.originalWeight = originalWeight;
	}

	public List<NullDistribution> getNullDistributionsOriginal() {
		return nullDistributionsOriginal;
	}

	public void setNullDistributionsOriginal(List<NullDistribution> nullDistributionsOriginal) {
		this.nullDistributionsOriginal = nullDistributionsOriginal;
	}

	public List<NullDistribution> getNullDistributionsCore() {
		return nullDistributionsCore;
	}

	public void setNullDistributionsCore(List<NullDistribution> nullDistributionsCore) {
		this.nullDistributionsCore = nullDistributionsCore;
	}

	public Double getCoreCv() {
		return coreCv;
	}

	public void setCoreCv(Double coreCv) {
		this.coreCv = coreCv;
	}

	public Double getCoreWeight() {
		return coreWeight;
	}

	public void setCoreWeight(Double coreWeight) {
		this.coreWeight = coreWeight;
	}

	public Double getPvalueCore() {
		return pvalueCore;
	}

	public void setPvalueCore(Double pvalueCore) {
		this.pvalueCore = pvalueCore;
	}

	public Double getFinalPvalue() {
		return finalPvalue;
	}

	public void setFinalPvalue(Double finalPvalue) {
		this.finalPvalue = finalPvalue;
	}

	public Double getFinalWeight() {
		return finalWeight;
	}

	public void setFinalWeight(Double finalWeight) {
		this.finalWeight = finalWeight;
	}
	
	public Double getQvalue() {
		return qvalue;
	}

	public void setQvalue(Double qvalue) {
		this.qvalue = qvalue;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((coreCv == null) ? 0 : coreCv.hashCode());
		result = prime * result + ((coreProteins == null) ? 0 : coreProteins.hashCode());
		result = prime * result + ((coreWeight == null) ? 0 : coreWeight.hashCode());
		result = prime * result + ((finalPvalue == null) ? 0 : finalPvalue.hashCode());
		result = prime * result + ((finalWeight == null) ? 0 : finalWeight.hashCode());
		result = prime * result + ((maxStatisticTest == null) ? 0 : maxStatisticTest.hashCode());
		result = prime * result + ((nullDistributionsCore == null) ? 0 : nullDistributionsCore.hashCode());
		result = prime * result + ((nullDistributionsOriginal == null) ? 0 : nullDistributionsOriginal.hashCode());
		result = prime * result + ((originalCv == null) ? 0 : originalCv.hashCode());
		result = prime * result + ((originalMeans == null) ? 0 : originalMeans.hashCode());
		result = prime * result + ((originalProteins == null) ? 0 : originalProteins.hashCode());
		result = prime * result + ((originalWeight == null) ? 0 : originalWeight.hashCode());
		result = prime * result + ((originalWeights == null) ? 0 : originalWeights.hashCode());
		result = prime * result + ((pvalueCore == null) ? 0 : pvalueCore.hashCode());
		result = prime * result + ((pvalueOriginal == null) ? 0 : pvalueOriginal.hashCode());
		result = prime * result + ((pvalueRecalculated == null) ? 0 : pvalueRecalculated.hashCode());
		result = prime * result + ((qvalue == null) ? 0 : qvalue.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
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
		GoTermCondition other = (GoTermCondition) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (coreCv == null) {
			if (other.coreCv != null)
				return false;
		} else if (!coreCv.equals(other.coreCv))
			return false;
		if (coreProteins == null) {
			if (other.coreProteins != null)
				return false;
		} else if (!coreProteins.equals(other.coreProteins))
			return false;
		if (coreWeight == null) {
			if (other.coreWeight != null)
				return false;
		} else if (!coreWeight.equals(other.coreWeight))
			return false;
		if (finalPvalue == null) {
			if (other.finalPvalue != null)
				return false;
		} else if (!finalPvalue.equals(other.finalPvalue))
			return false;
		if (finalWeight == null) {
			if (other.finalWeight != null)
				return false;
		} else if (!finalWeight.equals(other.finalWeight))
			return false;
		if (maxStatisticTest == null) {
			if (other.maxStatisticTest != null)
				return false;
		} else if (!maxStatisticTest.equals(other.maxStatisticTest))
			return false;
		if (nullDistributionsCore == null) {
			if (other.nullDistributionsCore != null)
				return false;
		} else if (!nullDistributionsCore.equals(other.nullDistributionsCore))
			return false;
		if (nullDistributionsOriginal == null) {
			if (other.nullDistributionsOriginal != null)
				return false;
		} else if (!nullDistributionsOriginal.equals(other.nullDistributionsOriginal))
			return false;
		if (originalCv == null) {
			if (other.originalCv != null)
				return false;
		} else if (!originalCv.equals(other.originalCv))
			return false;
		if (originalMeans == null) {
			if (other.originalMeans != null)
				return false;
		} else if (!originalMeans.equals(other.originalMeans))
			return false;
		if (originalProteins == null) {
			if (other.originalProteins != null)
				return false;
		} else if (!originalProteins.equals(other.originalProteins))
			return false;
		if (originalWeight == null) {
			if (other.originalWeight != null)
				return false;
		} else if (!originalWeight.equals(other.originalWeight))
			return false;
		if (originalWeights == null) {
			if (other.originalWeights != null)
				return false;
		} else if (!originalWeights.equals(other.originalWeights))
			return false;
		if (pvalueCore == null) {
			if (other.pvalueCore != null)
				return false;
		} else if (!pvalueCore.equals(other.pvalueCore))
			return false;
		if (pvalueOriginal == null) {
			if (other.pvalueOriginal != null)
				return false;
		} else if (!pvalueOriginal.equals(other.pvalueOriginal))
			return false;
		if (pvalueRecalculated == null) {
			if (other.pvalueRecalculated != null)
				return false;
		} else if (!pvalueRecalculated.equals(other.pvalueRecalculated))
			return false;
		if (qvalue == null) {
			if (other.qvalue != null)
				return false;
		} else if (!qvalue.equals(other.qvalue))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		return true;
	}
	
}
