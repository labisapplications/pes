package br.com.usp.labis.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Extension {

	List<ConnectedXrefs> connectedXrefs;

	public List<ConnectedXrefs> getConnectedXrefs() {
		return connectedXrefs;
	}

	public void setConnectedXrefs(List<ConnectedXrefs> connectedXrefs) {
		this.connectedXrefs = connectedXrefs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectedXrefs == null) ? 0 : connectedXrefs.hashCode());
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
		Extension other = (Extension) obj;
		if (connectedXrefs == null) {
			if (other.connectedXrefs != null)
				return false;
		} else if (!connectedXrefs.equals(other.connectedXrefs))
			return false;
		return true;
	}

}
