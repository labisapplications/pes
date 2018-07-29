package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<Replicate> replicates;
	private Double mean;
	private Double cv; //Coeficient of Variation 
	private Double weight;
	private Double sd; //standard deviation


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Replicate> getReplicates() {
		return replicates;
	}

	public void setReplicates(List<Replicate> replicates) {
		this.replicates = replicates;
	}

	public Double getMean() {
		return mean;
	}

	public void setMean(Double mean) {
		this.mean = mean;
	}

	public Double getCv() {
		return cv;
	}

	public void setCv(Double cv) {
		this.cv = cv;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	

	public Double getSd() {
		return sd;
	}

	public void setSd(Double sd) {
		this.sd = sd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((replicates == null) ? 0 : replicates.hashCode());
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
		Condition other = (Condition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (replicates == null) {
			if (other.replicates != null)
				return false;
		} else if (!replicates.equals(other.replicates))
			return false;
		return true;
	}
}
