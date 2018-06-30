package br.com.usp.labis.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GeneProductDatabaseEnum {
	
    @JsonProperty("UniProtKB")
	UNIPROTKB("UniProtKB"),
	
    @JsonProperty("UniProtKB-KW")
	UNIPROTKB_KW("UniProtKB-KW");

	private String database;

	GeneProductDatabaseEnum(String database) {
		this.database = database;
	}

	public String getDatabase() {
		return database;
	}

}
