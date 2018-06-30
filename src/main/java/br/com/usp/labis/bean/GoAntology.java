package br.com.usp.labis.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.usp.labis.enums.GoAspectEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoAntology {
	
	String goId;
	Boolean isObsolete;
	String name;
	GoAspectEnum aspect;

	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}

	public Boolean getIsObsolete() {
		return isObsolete;
	}

	public void setIsObsolete(Boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GoAspectEnum getAspect() {
		return aspect;
	}

	public void setAspect(GoAspectEnum aspect) {
		this.aspect = aspect;
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
		GoAntology other = (GoAntology) obj;
		if (goId == null) {
			if (other.goId != null)
				return false;
		} else if (!goId.equals(other.goId))
			return false;
		return true;
	}
}
