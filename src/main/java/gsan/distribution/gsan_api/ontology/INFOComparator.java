package gsan.distribution.gsan_api.ontology;

import java.util.Comparator;

public class INFOComparator implements Comparator<String> {
	
	int ic = 0;
	GlobalOntology go;
	public INFOComparator(int ic, GlobalOntology go) {
		this.ic = ic;
		this.go = go;
	}
	
    @Override
    public int compare(String o1, String o2) {
        return go.allStringtoInfoTerm.get(o2).ICs.get(ic).compareTo(go.allStringtoInfoTerm.get(o1).ICs.get(ic));
    }
    
}