package br.com.usp.labis.bean;

import java.util.List;

public class Condition {

	private String name;
	private List<Replicate> replicates;

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
