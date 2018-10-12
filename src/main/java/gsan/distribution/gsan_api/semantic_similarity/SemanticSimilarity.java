package gsan.distribution.gsan_api.semantic_similarity;

import java.util.*;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

//import org.apache.poi.poifs.crypt.standard.StandardEncryptionInfoBuilder;

public abstract class SemanticSimilarity {
	
	
	public abstract double method(String t1, String t2,GlobalOntology go) ;

	public static String[] interstingAncestor(String t1, String t2,GlobalOntology go,int position){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
		anc2.add(term2.toString());
		String[] ancestor = new String[2]; // 0 String LCA, 1 String MICA, 2 Double SPL
		
		
		if(!t1.equals(t2)){
				anc1.retainAll(anc2);
				List<String> ancs = new ArrayList<String>();
				List<Double> ds = new ArrayList<Double>();
				List<Double> ic = new ArrayList<Double>();
			
				for(String a1 : anc1){
					ds.add(term1.distancias.get(a1) + term2.distancias.get(a1));
					ic.add(go.allStringtoInfoTerm.get(a1).ICs.get(position));
					ancs.add(a1);
				}
			if(!ancs.isEmpty()) {
			
			    ancestor[1] = ancs.get(ic.indexOf((Collections.max(ic))));
			    double depth = -1;
			    String anc = null;
			    for(int i=0;i<ancs.size();i++) {
			    	if(ds.get(i)==Collections.min(ds) && go.allStringtoInfoTerm.get(ancs.get(i)).depth()>depth) {
			    		anc = ancs.get(i);
			    		depth = go.allStringtoInfoTerm.get(ancs.get(i)).depth();
			    	}
			    }
				ancestor[0] = anc;
				
			
			return ancestor;}
			else {
				ancestor[0] = null;
			    ancestor[1] = null;
				return ancestor;
			}
		}
		else{
			ancestor[0] = t2;
		    ancestor[1] = t2;
		    
			return ancestor;
		}
	}
	public static String mica(String t1, String t2,GlobalOntology go,int position){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
		anc2.add(term2.toString());
		
		if(!t1.equals(t2)){
				anc1.retainAll(anc2);
				List<String> ancs = new ArrayList<String>();
				List<Double> ds = new ArrayList<Double>();
				List<Double> ic = new ArrayList<Double>();
			
				for(String a1 : anc1){
					ds.add(term1.distancias.get(a1) + term2.distancias.get(a1));
					ic.add(go.allStringtoInfoTerm.get(a1).ICs.get(position));
					ancs.add(a1);
				}
			if(!ancs.isEmpty()) {
			
			    
			
			return ancs.get(ic.indexOf((Collections.max(ic))));
			}
			else {
				return null;
			}
		}
		else{
		    
			return t2;
		}
	}
	public static String lca(String t1, String t2,GlobalOntology go){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
		anc2.add(term2.toString());
		
		if(!t1.equals(t2)){
				anc1.retainAll(anc2);
				List<String> ancs = new ArrayList<String>();
				List<Double> ds = new ArrayList<Double>();
			
				for(String a1 : anc1){
					ds.add(term1.distancias.get(a1) + term2.distancias.get(a1));
					ancs.add(a1);
				}
			if(!ancs.isEmpty()) {
			
			    double depth = -1;
			    String anc = null;
			    for(int i=0;i<ancs.size();i++) {
			    	if(ds.get(i)==Collections.min(ds) && go.allStringtoInfoTerm.get(ancs.get(i)).depth()>depth) {
			    		anc = ancs.get(i);
			    		depth = go.allStringtoInfoTerm.get(ancs.get(i)).depth();
			    	}
			    }
				
			
			return anc;}
			else {
				return null;
			}
		}
		else{
		    
			return t2;
		}
	}
	
	public static double SVALUE(String t1, String t2,GlobalOntology go){

        InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
        InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
        Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
        anc1.addAll(term1.part_of.ancestors);
        anc1.add(term1.toString());
        Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
        anc2.addAll(term2.part_of.ancestors);
        anc2.add(term2.toString());
        double ssSvalue = 0.;
       
                anc1.retainAll(anc2);
           
                for(String a1 : anc1){
                    InfoTerm ianc = go.allStringtoInfoTerm.get(a1);
                    ssSvalue += (ianc.sValue.get(term1.toString()) +ianc.sValue.get(term2.toString()));


        }
            return ssSvalue;
	}
	public static double SVALUEIC(String t1, String t2,GlobalOntology go){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
		anc2.add(term2.toString());
		double ssSvalue = 0.;
		
				anc1.retainAll(anc2);
			
				for(String a1 : anc1){
					InfoTerm ianc = go.allStringtoInfoTerm.get(a1);
					ssSvalue += ianc.sValueIC;


		}
			return ssSvalue;


	}
}
