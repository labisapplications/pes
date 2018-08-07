package br.com.usp.labis.bean;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

	private String conditionName;
	private String goId;
	private String geneName;
	private String goName;
	private String qualifier;
	private String aspect;
	private String pvalue;
	private String qvalue;
	private String rank;
	private String core;
	private String weight;
	private String pvalueRatioAB;
	private String pvalueRatioBA;
	private String ratioBA;
	
	private String originalWeight;
	private String originalCore;
	private String originalPvalue;
	private String details;

	
	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getAspect() {
		return aspect;
	}

	public void setAspect(String aspect) {
		this.aspect = aspect;
	}

	public String getPvalue() {
		return pvalue;
	}

	public void setPvalue(String pvalue) {
		this.pvalue = pvalue;
	}

	public String getQvalue() {
		return qvalue;
	}

	public void setQvalue(String qvalue) {
		this.qvalue = qvalue;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getCore() {
		return core;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getGoName() {
		return goName;
	}

	public void setGoName(String goName) {
		this.goName = goName;
	}

	public String getRatioBA() {
		return ratioBA;
	}

	public void setRatioBA(String ratioBA) {
		this.ratioBA = ratioBA;
	}

	public String getPvalueRatioAB() {
		return pvalueRatioAB;
	}

	public void setPvalueRatioAB(String pvalueRatioAB) {
		this.pvalueRatioAB = pvalueRatioAB;
	}

	public String getPvalueRatioBA() {
		return pvalueRatioBA;
	}

	public void setPvalueRatioBA(String pvalueRatioBA) {
		this.pvalueRatioBA = pvalueRatioBA;
	}

	public String getOriginalWeight() {
		return originalWeight;
	}

	public void setOriginalWeight(String originalWeight) {
		this.originalWeight = originalWeight;
	}

	public String getOriginalCore() {
		return originalCore;
	}

	public void setOriginalCore(String originalCore) {
		this.originalCore = originalCore;
	}

	public String getOriginalPvalue() {
		return originalPvalue;
	}

	public void setOriginalPvalue(String originalPvalue) {
		this.originalPvalue = originalPvalue;
	}
	

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aspect == null) ? 0 : aspect.hashCode());
		result = prime * result + ((conditionName == null) ? 0 : conditionName.hashCode());
		result = prime * result + ((core == null) ? 0 : core.hashCode());
		result = prime * result + ((geneName == null) ? 0 : geneName.hashCode());
		result = prime * result + ((goId == null) ? 0 : goId.hashCode());
		result = prime * result + ((goName == null) ? 0 : goName.hashCode());
		result = prime * result + ((originalCore == null) ? 0 : originalCore.hashCode());
		result = prime * result + ((originalPvalue == null) ? 0 : originalPvalue.hashCode());
		result = prime * result + ((originalWeight == null) ? 0 : originalWeight.hashCode());
		result = prime * result + ((pvalue == null) ? 0 : pvalue.hashCode());
		result = prime * result + ((pvalueRatioAB == null) ? 0 : pvalueRatioAB.hashCode());
		result = prime * result + ((pvalueRatioBA == null) ? 0 : pvalueRatioBA.hashCode());
		result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
		result = prime * result + ((qvalue == null) ? 0 : qvalue.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((ratioBA == null) ? 0 : ratioBA.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
		Result other = (Result) obj;
		if (aspect == null) {
			if (other.aspect != null)
				return false;
		} else if (!aspect.equals(other.aspect))
			return false;
		if (conditionName == null) {
			if (other.conditionName != null)
				return false;
		} else if (!conditionName.equals(other.conditionName))
			return false;
		if (core == null) {
			if (other.core != null)
				return false;
		} else if (!core.equals(other.core))
			return false;
		if (geneName == null) {
			if (other.geneName != null)
				return false;
		} else if (!geneName.equals(other.geneName))
			return false;
		if (goId == null) {
			if (other.goId != null)
				return false;
		} else if (!goId.equals(other.goId))
			return false;
		if (goName == null) {
			if (other.goName != null)
				return false;
		} else if (!goName.equals(other.goName))
			return false;
		if (originalCore == null) {
			if (other.originalCore != null)
				return false;
		} else if (!originalCore.equals(other.originalCore))
			return false;
		if (originalPvalue == null) {
			if (other.originalPvalue != null)
				return false;
		} else if (!originalPvalue.equals(other.originalPvalue))
			return false;
		if (originalWeight == null) {
			if (other.originalWeight != null)
				return false;
		} else if (!originalWeight.equals(other.originalWeight))
			return false;
		if (pvalue == null) {
			if (other.pvalue != null)
				return false;
		} else if (!pvalue.equals(other.pvalue))
			return false;
		if (pvalueRatioAB == null) {
			if (other.pvalueRatioAB != null)
				return false;
		} else if (!pvalueRatioAB.equals(other.pvalueRatioAB))
			return false;
		if (pvalueRatioBA == null) {
			if (other.pvalueRatioBA != null)
				return false;
		} else if (!pvalueRatioBA.equals(other.pvalueRatioBA))
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
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
		if (ratioBA == null) {
			if (other.ratioBA != null)
				return false;
		} else if (!ratioBA.equals(other.ratioBA))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}

	

}
