package gsan.distribution.gsan_api.semantic_similarity;

import java.util.HashSet;
import java.util.Set;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class DF extends SemanticSimilarity {
	
	
public double method(String t1, String t2,GlobalOntology go) {

		InfoTerm termino1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm termino2 = go.allStringtoInfoTerm.get(t2);
		Set<String> ancu = new HashSet<String>(termino1.is_a.ancestors);
		Set<String> anci = new HashSet<String>(ancu);
		Set<String> anc2 = new HashSet<String>(termino2.is_a.ancestors);
		ancu.addAll(anc2);
		anci.retainAll(anc2);
		return (1.0 - ((double)(ancu.size()-anci.size())/ancu.size()) );
	}

}