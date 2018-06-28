package br.com.usp.labis.bean;

import java.util.List;

public class Protein {

	private String proteinId;
	private Float anovaSignificant;
	private Float ttestSignificant;
	private Float anovaPvalue;
	private Float ttestPvalue;
	private String geneNames;
	private List<Condition> conditions;

	public String getProteinId() {
		return proteinId;
	}

	public void setProteinId(String proteinId) {
		this.proteinId = proteinId;
	}

	public Float getAnovaSignificant() {
		return anovaSignificant;
	}

	public void setAnovaSignificant(Float anovaSignificant) {
		this.anovaSignificant = anovaSignificant;
	}

	public Float getTtestSignificant() {
		return ttestSignificant;
	}

	public void setTtestSignificant(Float ttestSignificant) {
		this.ttestSignificant = ttestSignificant;
	}

	public Float getAnovaPvalue() {
		return anovaPvalue;
	}

	public void setAnovaPvalue(Float anovaPvalue) {
		this.anovaPvalue = anovaPvalue;
	}

	public Float getTtestPvalue() {
		return ttestPvalue;
	}

	public void setTtestPvalue(Float ttestPvalue) {
		this.ttestPvalue = ttestPvalue;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public String getGeneNames() {
		return geneNames;
	}

	public void setGeneNames(String geneNames) {
		this.geneNames = geneNames;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneNames == null) ? 0 : geneNames.hashCode());
		result = prime * result + ((proteinId == null) ? 0 : proteinId.hashCode());
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
		Protein other = (Protein) obj;
		if (geneNames == null) {
			if (other.geneNames != null)
				return false;
		} else if (!geneNames.equals(other.geneNames))
			return false;
		if (proteinId == null) {
			if (other.proteinId != null)
				return false;
		} else if (!proteinId.equals(other.proteinId))
			return false;
		return true;
	}


}
