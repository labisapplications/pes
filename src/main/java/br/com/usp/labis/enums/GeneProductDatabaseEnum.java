package br.com.usp.labis.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GeneProductDatabaseEnum {
	
    @JsonProperty("UniProtKB")
	UNIPROTKB("UniProtKB"),
	
	INTERPRO("InterPro"),
	ENSEMBLE("ensembl"),
	CL("CL"),
	
    @JsonProperty("UniProtKB-KW")
	UNIPROTKB_KW("UniProtKB-KW");

	private String database;

	GeneProductDatabaseEnum(String database) {
		this.database = database;
	}

    @JsonValue
	public String getDatabase() {
		return database;
	}

}
