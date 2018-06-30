package br.com.usp.labis.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoSearchResult {

	@JsonProperty
	Integer numberOfHits;

	@JsonProperty
	List<Object> results;

	public Integer getNumberOfHits() {
		return numberOfHits;
	}

	public void setNumberOfHits(Integer numberOfHits) {
		this.numberOfHits = numberOfHits;
	}

	public List<Object> getResults() {
		return results;
	}

	public void setResults(List<Object> results) {
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
		GoSearchResult other = (GoSearchResult) obj;
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
