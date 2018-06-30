package br.com.usp.labis.enums;

public enum QualifierEnum {
	
	COLOCALIZES_WITH("colocalizes_with"), 
	REGULATES("regulates"), 
	TESTE("teste") ;

	private String qualifier;

	QualifierEnum(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getQualifier() {
		return qualifier;
	}
}
