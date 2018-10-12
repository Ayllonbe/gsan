package gsan.distribution.gsan_api.run.representative;


import java.text.DecimalFormat;
import java.util.Set;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class Representative {
	static DecimalFormat df = new DecimalFormat("0.00");
	
	public String id;
	public InfoTerm repesentative;
	public Set<InfoTerm> terms;
	public Representative(String id, InfoTerm r, Set<InfoTerm> t) {
		this.id = id;
		this.repesentative = r;
		this.terms = t;
	}
	
	/**
	 * Allow get the set of representative of each cluster 
	 * @param ic
	 * @param sub
	 * @param mSS
	 * @param mhcl
	 * @param geneset
	 * @param resfolder
	 * @param statfolder
	 * @param go
	 * @param terminos
	 * @param tailmin
	 * @param filtre
	 * @param precision
	 * @return
	 * @throws Exception
	 */
	

}