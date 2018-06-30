package br.com.usp.labis.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.usp.labis.enums.GoAspectEnum;
import br.com.usp.labis.enums.GoEvidenceEnum;
import br.com.usp.labis.enums.QualifierEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoAnnotation {

	String annotationId;
	String geneProductId;
	QualifierEnum qualifier;
	GoAspectEnum goAspect;
	GoEvidenceEnum goEvidence;
	String goId;
	String goName;
	Integer taxonId;
	String symbol;
	String name;
	Extension extensions;
	WithFrom withFrom;

	public String getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(String annotationId) {
		this.annotationId = annotationId;
	}

	public String getGeneProductId() {
		return geneProductId;
	}

	public void setGeneProductId(String geneProductId) {
		this.geneProductId = geneProductId;
	}

	public QualifierEnum getQualifier() {
		return qualifier;
	}

	public void setQualifier(QualifierEnum qualifier) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Extension getExtensions() {
		return extensions;
	}

	public void setExtensions(Extension extensions) {
		this.extensions = extensions;
	}

	public WithFrom getWithFrom() {
		return withFrom;
	}

	public void setWithFrom(WithFrom withFrom) {
		this.withFrom = withFrom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((goId == null) ? 0 : goId.hashCode());
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
		if (goId == null) {
			if (other.goId != null)
				return false;
		} else if (!goId.equals(other.goId))
			return false;
		return true;
	}

}
