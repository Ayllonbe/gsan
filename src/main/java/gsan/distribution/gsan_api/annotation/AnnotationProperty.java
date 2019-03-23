package gsan.distribution.gsan_api.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class AnnotationProperty implements Serializable{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	String id;
	String id_normalCase;
	public String symbol;
	String name; // Creo que hay mas de un nombre pues esta relacionada a la proteina y no al gen
	public double idf; // Lo calculo en Annotation.java y ya normalizado. 
	public Map<String,Set<String>> associations;
	
	String BD = "GO";

//	public AnnotationProperty(String id, String symbol, String name, List<String> terms){
//		this.id = id;
//		this.symbol = symbol;
//		this.name = name;
//		this.associations = new HashMap<>();
//		//this.evidence = new HashSet<String>(evidence);
//	}
	public AnnotationProperty(String id){
		this.id = id;
		this.symbol = new String();
		this.name = new String();
		this.associations = new HashMap<>();
		//this.evidence = new ArrayList<String>();
	}
	public String getSymbol(){
		return this.symbol;
	}
	public void setSymbol(String s){
		this.symbol = s;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String n){
		this.name = n;
	}
	public String toString(){
		return this.id;
	}
	
	public List<String> getTerms(String ontology){
		if(this.associations.containsKey(ontology)) {
		return new ArrayList<String>(this.associations.get(ontology));
		}
		else {
			return new ArrayList<String>();
		}
	}
	public void setAllTerms(Set<String> t,String ontology){

		if(this.associations.containsKey(ontology)) {
			this.associations.get(ontology).addAll(t);
		}else {
			this.associations.put(ontology, new HashSet<>());
			this.associations.get(ontology).addAll(t);
		}

	}
	public void setTerm(String t,String ontology){

		if(this.associations.containsKey(ontology)) {
			this.associations.get(ontology).add(t);
		}else {
			this.associations.put(ontology, new HashSet<>());
			this.associations.get(ontology).add(t);
		}
		
		

	}

	public Set<String> getTerms(){
		Set<String> setTerms = new HashSet<String>();
		for(String ont : this.associations.keySet()) {
			setTerms.addAll(this.associations.get(ont));
		}
		
		return setTerms;			

	}
	
	
	
	

}
