package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.usp.labis.enums.GoAspectEnum;
import br.com.usp.labis.enums.GoEvidenceEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GoAnnotation implements Serializable {
	
	@JsonProperty
	private String id;
	
	@JsonProperty
	private String geneProductId;
	
	@JsonProperty
	private String qualifier;
	
	@JsonProperty
	private GoAspectEnum goAspect;
	
	@JsonProperty
	private GoEvidenceEnum goEvidence;
	
	@JsonProperty
	private String goId;
	
	@JsonProperty
	private String goName;
	
	@JsonProperty
	private Integer taxonId;
	
	@JsonProperty
	private String symbol;
	
	/*@JsonProperty
	List<Extension> extensions;
	
	@JsonProperty
	List<WithFrom> withFrom;
	*/
	@JsonIgnore
	List<GoAntology> goAntology;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGeneProductId() {
		return geneProductId;
	}

	public void setGeneProductId(String geneProductId) {
		this.geneProductId = geneProductId;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public GoAspectEnum getGoAspect() {
		return goAspect;
	}

	public void setGoAspect(GoAspectEnum goAspect) {
		this.goAspect = goAspect;
	}

	public GoEvidenceEnum getGoEvidence() {
		return goEvidence;
	}

	public void setGoEvidence(GoEvidenceEnum goEvidence) {
		this.goEvidence = goEvidence;
	}

	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}

	public String getGoName() {
		return goName;
	}

	public void setGoName(String goName) {
		this.goName = goName;
	}

	public Integer getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(Integer taxonId) {
		this.taxonId = taxonId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/*
	public List<Extension> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<Extension> extensions) {
		this.extensions = extensions;
	}

	public List<WithFrom> getWithFrom() {
		return withFrom;
	}

	public void setWithFrom(List<WithFrom> withFrom) {
		this.withFrom = withFrom;
	}*/

	public List<GoAntology> getGoAntology() {
		return goAntology;
	}

	public void setGoAntology(List<GoAntology> goAntology) {
		this.goAntology = goAntology;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		GoAnnotation other = (GoAnnotation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

}
