package gsan.distribution.gsan_api.semantic_similarity;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class Ganesan extends SemanticSimilarity {
	
	
public double method(String t1, String t2,GlobalOntology go) {

	String ancestors = lca(t1, t2, go);
	InfoTerm lca = go.allStringtoInfoTerm.get(ancestors);
	return 2.*(lca.depth()+1.)/(go.allStringtoInfoTerm.get(t1).depth()+1.+go.allStringtoInfoTerm.get(t2).depth()+1.);
}
}