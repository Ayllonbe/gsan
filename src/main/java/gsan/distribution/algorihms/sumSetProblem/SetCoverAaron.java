package gsan.distribution.algorihms.sumSetProblem;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import gsan.distribution.gsan_api.ontology.GlobalOntology;


public class SetCoverAaron {

	

	public static List<String> scp(Map<String,BitSet> term2genebs, GlobalOntology go, List<String> ontology) {
		
		
		Set<String> res = new HashSet<>();
		for(String top:ontology) {
		BitSet cover = new BitSet();
		List<String> allTerms = new ArrayList<String>();
		for(Entry<String, BitSet> sbs  : term2genebs.entrySet()) {
			if(go.allStringtoInfoTerm.get(sbs.getKey()).top.equals(top)) {
//			System.out.println(go.allStringtoInfoTerm.get(sbs.getKey()).toName() + " : "  + go.allStringtoInfoTerm.get(sbs.getKey()).ICs.get(4)/go.allStringtoInfoTerm.get(sbs.getKey()).sIC + " : "  + sbs.getValue().cardinality());
//			System.out.println("here " + go.allStringtoInfoTerm.get(sbs.getKey()).getRegulatesClass());
			cover.or(sbs.getValue());
			allTerms.add(sbs.getKey());
			}
		}
		
		/*
		 * TEST
		 */
		List<String> essentials = new ArrayList<String>();
		for(String t : allTerms) {
			Set<String> othersTermsTot = new HashSet<String>(allTerms);
			othersTermsTot.remove(t);
			BitSet genes = new BitSet();
			for(String ot : othersTermsTot) {
				genes.or(term2genebs.get(ot));
			}
			
			if(genes.cardinality()!=cover.cardinality()) {
				essentials.add(t);
			}
			
			
		}
		
		/*
		 * END TEST
		 */
		
		

		BitSet finalBS = new BitSet();
		
		
		
			
			for(String e : essentials) {
			//System.out.println("E " + go.allStringtoInfoTerm.get(e).toName());
				res.add(e);
				allTerms.remove(allTerms.indexOf(e));
				finalBS.or(term2genebs.get(e));
			}
//		boolean stop = false;
//		if(finalBS.equals(cover)) {
//			stop = true;
//		}
		while(!finalBS.equals(cover)) {

			List<Double> li = new ArrayList<>();
			for(String t  : allTerms) {
				BitSet intersection = new BitSet();
				intersection.or(term2genebs.get(t));
				intersection.andNot(finalBS);
				
						
				if(intersection.equals(finalBS)) {
					li.add(0.);
				}else {
					//li.add((double)pivot.cardinality()/t2IC.get(t));	
					//System.out.println(t + " " +(double)pivot.cardinality()/t2Iv.get(t));
					li.add((double)intersection.cardinality()*(go.allStringtoInfoTerm.get(t).ICs.get(4)/go.allStringtoInfoTerm.get(t).sIC));
					//li.add((go.allStringtoInfoTerm.get(t).ICs.get(4))/go.allStringtoInfoTerm.get(t).sIC);
					

					//li.add((go.allStringtoInfoTerm.get(t).sIC/go.allStringtoInfoTerm.get(t).ICs.get(4))/(double)pivot.cardinality());
					
				}	
			}
			//System.out.println("\t" + Collections.max(li));
			String choosen = allTerms.get(li.indexOf(Collections.max(li)));
			res.add(choosen);
			allTerms.remove(li.indexOf(Collections.max(li)));
			finalBS.or(term2genebs.get(choosen));
			
			}

	}
		return new LinkedList<>(res);


	
	}

}
