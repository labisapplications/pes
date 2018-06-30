package br.com.usp.labis.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Protein {

	private String proteinId;
	private Float anovaSignificant;
	private Float ttestSignificant;
	private Float anovaPvalue;
	private Float ttestPvalue;
	private String geneName;
	private List<Condition> conditions;
	private List<GoAnnotation> goAnnotations;
	private Integer taxonId;

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
		return geneName;
	}

	public void setGeneNames(String geneName) {
		this.geneName = geneName;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public List<GoAnnotation> getGoAnnotations() {
		return goAnnotations;
	}

	public void setGoAnnotations(List<GoAnnotation> goAnnotations) {
		this.goAnnotations = goAnnotations;
	}

	public Integer getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(Integer taxonId) {
		this.taxonId = taxonId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneName == null) ? 0 : geneName.hashCode());
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
		if (geneName == null) {
			if (other.geneName != null)
				return false;
		} else if (!geneName.equals(other.geneName))
			return false;
		if (proteinId == null) {
			if (other.proteinId != null)
				return false;
		} else if (!proteinId.equals(other.proteinId))
			return false;
		return true;
	}


}
