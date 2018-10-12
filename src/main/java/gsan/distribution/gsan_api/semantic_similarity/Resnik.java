package gsan.distribution.gsan_api.semantic_similarity;


import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class Resnik extends SemanticSimilarity {
		
	
	public double method(String t1, String t2,GlobalOntology go) {

		String ancestors = mica(t1, t2, go, 4);
		InfoTerm mica = go.allStringtoInfoTerm.get(ancestors);
		
		return mica.ICs.get(4)/go.subontology.get(mica.top).maxIC(4);
	}
}
