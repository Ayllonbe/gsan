package gsan.distribution.gsan_api.run.representative;

import java.util.LinkedList;
import java.util.List;

import gsan.distribution.gsan_api.ontology.InfoTerm;

public class Cluster {

	
	public List<InfoTerm> representatives;
	public List<InfoTerm> terms;
	public List<String> genes;
	public double silhouette;
	
	public Cluster() {
		this.representatives = new LinkedList<>();
		this.terms = new LinkedList<>();
		this.genes = new LinkedList<>();
		this.silhouette = 0.;
	}
	
	
	
}
