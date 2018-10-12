package gsan.distribution.gsan_api.semantic_similarity;


import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class NUnivers_Mod extends SemanticSimilarity {
		
	
	public double method(String t1, String t2,GlobalOntology go) {

		String ancestors = mica(t1, t2, go, 3);
		InfoTerm mica = go.allStringtoInfoTerm.get(ancestors);
		return mica.ICs.get(3)>go.getPercentile(25, "mazandu",mica.top)?mica.ICs.get(3)/Math.max(go.allStringtoInfoTerm.get(t1).ICs.get(3), go.allStringtoInfoTerm.get(t2).ICs.get(3)):0;
		}
}
