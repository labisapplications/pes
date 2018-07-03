package br.com.usp.labis.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GoAspectEnum {

	BIOLOGICAL_PROCESS("biological_process"),
	MOLECULAR_FUNCTION("molecular_function"), 
	CELULAR_COMPONENT("cellular_component");

	private String aspect;

	GoAspectEnum(String aspect) {
		this.aspect = aspect;
	}

	@JsonValue
	public String getAspect() {
		return aspect;
	}

}
