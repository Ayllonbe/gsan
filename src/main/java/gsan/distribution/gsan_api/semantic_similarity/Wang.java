package gsan.distribution.gsan_api.semantic_similarity;

import gsan.distribution.gsan_api.ontology.GlobalOntology;

public class Wang extends SemanticSimilarity {
	
	public double method(String t1, String t2,GlobalOntology go) {

		return  SVALUE(t1, t2, go)/(go.allStringtoInfoTerm.get(t1).semanticValue + go.allStringtoInfoTerm.get(t2).semanticValue);
        
	}
}
