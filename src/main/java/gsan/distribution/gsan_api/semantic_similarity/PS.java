package gsan.distribution.gsan_api.semantic_similarity;


import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class PS extends SemanticSimilarity {
		
	
	public double method(String t1, String t2,GlobalOntology go) {

		String ancestors = lca(t1, t2, go);
		InfoTerm lca = go.allStringtoInfoTerm.get(ancestors);
		Double spl = go.allStringtoInfoTerm.get(t1).distancias.get(lca.toString()) + go.allStringtoInfoTerm.get(t2).distancias.get(lca.toString());
		return (double)lca.distancias.get(lca.top)/((double)lca.distancias.get(lca.top)+spl);
		
		
		
	}
}
