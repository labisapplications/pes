package br.com.usp.labis.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.usp.labis.enums.GeneProductDatabaseEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneProduct {

	@JsonProperty
	GeneProductDatabaseEnum database;
	
	@JsonProperty
	String id;
	
	@JsonProperty
	String symbol;
	
	@JsonProperty
	String name;
	
	//@JsonProperty
	//List<String> synonyms;
	
	@JsonProperty
	String type;
	
	@JsonProperty
	Integer taxonId;
	
	@JsonProperty
	String databaseSubset;

	public GeneProductDatabaseEnum getDatabase() {
		return database;
	}

	public void setDatabase(GeneProductDatabaseEnum database) {
		this.database = database;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(Integer taxonId) {
		this.taxonId = taxonId;
	}

	public String getDatabaseSubset() {
		return databaseSubset;
	}

	public void setDatabaseSubset(String databaseSubset) {
		this.databaseSubset = databaseSubset;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((database == null) ? 0 : database.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((taxonId == null) ? 0 : taxonId.hashCode());
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
		GeneProduct other = (GeneProduct) obj;
		if (database != other.database)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (taxonId == null) {
			if (other.taxonId != null)
				return false;
		} else if (!taxonId.equals(other.taxonId))
			return false;
		return true;
	}

	
}
