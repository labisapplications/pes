package br.com.usp.labis.enums;

public enum GoEvidenceEnum {
	
	DIRECT_ASSAY("IDA"), 
	EXPERIMENT("EXP"), 
	PHYSICAL_INTERACTION("IPI"),
	MUTANT_PHENOTYPE("IMP"),
	GENETIC_INTERACTION("IGI"),
	EXPRESSION_PATTERN("IEP") ;

	private String evidence;

	GoEvidenceEnum(String evidence) {
		this.evidence = evidence;
	}

	public String getGoEvidence() {
		return evidence;
	}
}
