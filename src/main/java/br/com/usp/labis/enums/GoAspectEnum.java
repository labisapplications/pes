package br.com.usp.labis.enums;

public enum GoAspectEnum {

	BIOLOGICAL_PROCESS("biological process"), 
	MOLECULAR_FUNCTION("molecular function"), 
	CELULAR_COMPONENT("cellular component") ;

	private String aspect;

	GoAspectEnum(String aspect) {
		this.aspect = aspect;
	}

	public String getAspect() {
		return aspect;
	}

}
