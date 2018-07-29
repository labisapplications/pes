package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoSearchGoAntologyResult implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private Integer numberOfHits;

	@JsonProperty
	private List<GoAntology> results;

	public Integer getNumberOfHits() {
		return numberOfHits;
	}

	public void setNumberOfHits(Integer numberOfHits) {
		this.numberOfHits = numberOfHits;
	}

	public List<GoAntology> getResults() {
		return results;
	}

	public void setResults(List<GoAntology> results) {
		this.results = results;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numberOfHits == null) ? 0 : numberOfHits.hashCode());
		result = prime * result + ((results == null) ? 0 : results.hashCode());
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
		GoSearchGoAntologyResult other = (GoSearchGoAntologyResult) obj;
		if (numberOfHits == null) {
			if (other.numberOfHits != null)
				return false;
		} else if (!numberOfHits.equals(other.numberOfHits))
			return false;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		return true;
	}


}
