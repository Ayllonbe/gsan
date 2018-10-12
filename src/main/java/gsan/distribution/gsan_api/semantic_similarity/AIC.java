package gsan.distribution.gsan_api.semantic_similarity;

import gsan.distribution.gsan_api.ontology.GlobalOntology;

public class AIC extends SemanticSimilarity {
	
	public double method(String t1, String t2,GlobalOntology go) {

		return  2*SVALUEIC(t1, t2, go)/(go.allStringtoInfoTerm.get(t1).aggregateIC+go.allStringtoInfoTerm.get(t2).aggregateIC);
	
	}
}
