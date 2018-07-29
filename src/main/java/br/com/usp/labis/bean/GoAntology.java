package br.com.usp.labis.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.usp.labis.enums.GoAspectEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoAntology  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Boolean isObsolete;
	private String name;
	private GoAspectEnum aspect;
	private List<GoAntologyChild> children;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public List<GoAntologyChild> getChildren() {
		return children;
	}
	public void setChildren(List<GoAntologyChild> children) {
		this.children = children;
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
		GoAntology other = (GoAntology) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
