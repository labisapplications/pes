package br.com.usp.labis.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QualifierEnum {
	
	COLOCALIZES_WITH("colocalizes_with"), 
	REGULATES("regulates"), 
	IS_A("is_a") ,
	PART_OF("part_of"),
	CONTRIBUTES_TO("contributes_to"), 
	NOT("NOT");

	private String qualifier;

	QualifierEnum(String qualifier) {
		this.qualifier = qualifier;
	}

    @JsonValue
	public String getQualifier() {
		return qualifier;
	}
}
