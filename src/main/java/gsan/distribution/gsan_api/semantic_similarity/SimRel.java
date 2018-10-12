package gsan.distribution.gsan_api.semantic_similarity;


import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class SimRel extends SemanticSimilarity {
		
	
	public double method(String t1, String t2,GlobalOntology go) {

		String ancestors = mica(t1, t2, go, 4);
		InfoTerm mica = go.allStringtoInfoTerm.get(ancestors);
		
		return (2.0*(((double)mica.ICs.get(4))/((double)(go.allStringtoInfoTerm.get(t1).ICs.get(4)+go.allStringtoInfoTerm.get(t2).ICs.get(4)))))*(1.-Math.exp(-mica.ICs.get(4)));
	}
}
