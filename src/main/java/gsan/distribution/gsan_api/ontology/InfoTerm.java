package gsan.distribution.gsan_api.ontology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;


import gsan.distribution.algorihms.astar.*;

public class InfoTerm implements Comparable<InfoTerm>, Serializable {

	/*
	 * This class permit get and keep whole interest informations of a term.
	 * Variables:
	 * 	
	 * owlclass						=> It's the IRI of term						| network			=> List of IRIs terms network connected
	 * name    						=> Term's name								| prof 				=> Deep position 
	 * ancestor /parentDirect 		=> List of IRIs term's ancestor /parents	| regulatesTotal	=> List of IRIs terms' regulates
	 * descendant /childrenDirect 	=> List of IRIs term's descendant /children	| partOfTotal		=> List of IRIs terms' part of
	 * distDisktra 					=> List Parents' Disktra distance			|
	 * regulates					=> List of IRIS term's regulated			|
	 * partOf						=> List of IRIS term's with part			|
	 * 
	 */
 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5356804658328772065L;
	public String id = new String();
	public double g_scores;
    public double h_scores;
    public double f_scores = 0;
    public Edges[] adjacencies;
    public InfoTerm next;
    public String top;
	public String name= new String();
	public Link is_a;
	public Link part_of;
	public Link both;
	public BitSet bits;
	public Set<String> termcombi = new HashSet<String>();;
	public boolean inProk = false;
//	List<String> hasPart;
	public ArrayList<Double> ICs; // 0 nuno 1 zhou 

	String[] alphaBetaMazandu = new String[2];
	Set<String> proteinsgenome;
	public String positiveR;
	public String negativeR;
	public String regulate;
	public Hashtable<String,Double> distancias;
	public Set<String> geneSet;
	public Set<String> genome;
	public Map<String,Double> sValue;
	public double sValueIC = 0;
	public double semanticValue = 0;
	public double aggregateIC = 0;
	public double sIC = 0.;
	public double ivalue = 0.;

	public InfoTerm(){
		this.id = new String();
		this.name=new String();
		this.is_a = new Link();
		this.both = new Link();
		this.part_of = new Link();
		this.regulate = new String();
		this.positiveR= new String();
		this.negativeR = new String();
		this.h_scores = 0.;
		//this.hasPart = new ArrayList<String>();
		this.proteinsgenome = new HashSet<String>();
		this.ICs  = new ArrayList<Double>();
		this.distancias = new Hashtable<String,Double>();
		this.geneSet = new HashSet<String>();
		this.genome = new HashSet<String>();
		this.bits = new BitSet();
	}
	public InfoTerm(String id, double level,boolean prok){
		this.id = id;
		this.name=new String();
		this.is_a = new Link();
		this.part_of = new Link();
		this.both = new Link();
		this.regulate = new String();
		this.positiveR= new String();
		this.negativeR = new String();
		this.h_scores = level;
		this.inProk = prok;
		//this.hasPart = new ArrayList<String>();
		this.proteinsgenome = new HashSet<String>();
		this.ICs  = new ArrayList<Double>();
		this.distancias = new Hashtable<String,Double>();
		this.geneSet = new HashSet<String>();
		this.genome = new HashSet<String>();
		this.bits = new BitSet();
		this.sValue = new HashMap<>();
	}
	public InfoTerm(String id, double level){
		this.id = id;
		this.name=new String();
		this.is_a = new Link();
		this.part_of = new Link();
		this.both = new Link();
		this.regulate = new String();
		this.positiveR= new String();
		this.negativeR = new String();
		this.h_scores = level;
		//this.hasPart = new ArrayList<String>();
		this.proteinsgenome = new HashSet<String>();
		this.ICs  = new ArrayList<Double>();
		this.distancias = new Hashtable<String,Double>();
		this.geneSet = new HashSet<String>();
		this.genome = new HashSet<String>();
		this.bits = new BitSet();
		this.sValue = new HashMap<>();
	}
	public InfoTerm(InfoTerm it){
		this.id = it.id;
		this.name=new String(it.name);
		this.is_a = new Link(it.is_a);
		this.part_of = new Link(it.part_of);
		this.both = new Link(it.both);
		this.regulate = new String(it.regulate);
		this.positiveR= new String(it.positiveR);
		this.negativeR = new String(it.negativeR);
		this.h_scores = it.h_scores;
		this.inProk = it.inProk;
		//this.hasPart = new ArrayList<String>();
		this.proteinsgenome = new HashSet<String>(it.proteinsgenome);
		this.ICs  = new ArrayList<Double>(it.ICs);
		this.distancias = new Hashtable<String,Double>(it.distancias);
		this.geneSet = new HashSet<String>();
		this.genome = new HashSet<String>();
		this.bits = new BitSet();
		this.sValue = new HashMap<>(it.sValue);
		this.top = it.top;
	}
	public InfoTerm(List<InfoTerm> ti){
		Set<String> anc = new HashSet<String>();
		Set<String> pere= new HashSet<String>();
		Set<String> chil = new HashSet<String>();
		Set<String> chillea = new HashSet<String>();
		Set<String> desc = new HashSet<String>();
		Set<String> ancpof = new HashSet<String>();
		Set<String> perepof= new HashSet<String>();
		Set<String> chilpof = new HashSet<String>();
		Set<String> chilleapof = new HashSet<String>();
		Set<String> descpof = new HashSet<String>();
		Set<String> combi = new HashSet<String>();
		Set<String> genome = new HashSet<String>();
		Set<String> g = new HashSet<String>();
		String id = new String();
		String name = new String();
		this.bits = new BitSet();
		double[] d = new double[ti.size()];
		this.ICs = new ArrayList<Double>();
		
		double nuno = 0.;
		double zhou= 0.;
		double sanchez= 0.;
		double resnick= 0.;
		double resnickZhou= 0.;
		double nunoOrg= 0.;
		
		int i = 0;
		
		for(InfoTerm t : ti){
		id += t.id + "-";
		name +=t.name + "_";
		d[i] = t.depth();
		g.addAll(t.geneSet);
		genome.addAll(t.genome);
		i++;
		anc.addAll(t.is_a.ancestors);
		combi.add(t.toString());
		chillea.addAll(t.is_a.descLeaves);
		desc.add(t.toString());
		desc.addAll(t.is_a.descendants);
		chil.add(t.toString());
		chil.addAll(t.is_a.childrens);
		pere.addAll(t.is_a.parents);
		
		ancpof.addAll(t.part_of.ancestors);
		chilleapof.addAll(t.part_of.descLeaves);
		descpof.addAll(t.part_of.descendants);
		chilpof.addAll(t.part_of.childrens);
		perepof.addAll(t.part_of.parents);
		
		nuno += t.ICs.get(0);
		zhou += t.ICs.get(1);
		sanchez += t.ICs.get(2);
		resnick += t.ICs.get(3);
		resnickZhou += t.ICs.get(4);
		nunoOrg += t.ICs.get(5);
		
		this.bits.or(t.bits);
		}
		this.geneSet = new HashSet<String>(g);
		this.genome = new HashSet<String>(genome);
		this.h_scores = average(d);
		this.id = id.substring(0, id.length()-1);
		this.name = name.substring(0, name.length()-1);
		this.is_a = new Link();
		this.is_a.ancestors.addAll(anc);
		this.is_a.descendants.addAll(desc);
		this.is_a.parents.addAll(pere);
		this.is_a.childrens.addAll(chil);
		this.is_a.descLeaves.addAll(chillea);
		this.part_of = new Link();
		this.part_of.ancestors.addAll(ancpof);
		this.part_of.descendants.addAll(descpof);
		this.part_of.parents.addAll(perepof);
		this.part_of.childrens.addAll(chilpof);
		this.part_of.descLeaves.addAll(chilleapof);
		this.both = new Link();
		this.Both();
		this.termcombi = new HashSet<String>(combi);
		this.top = ti.get(0).top;
		this.regulate = new String();
		this.positiveR= new String();
		this.negativeR = new String();
		
		
		
		this.ICs.add(nuno/ti.size());
		this.ICs.add(zhou/ti.size());
		this.ICs.add(sanchez/ti.size());
		this.ICs.add(resnick/ti.size());
		this.ICs.add(resnickZhou/ti.size());
		this.ICs.add(nunoOrg/ti.size());
		
	}
	
	private double average(double[] vector){
		double i = 0;
		for(double v : vector ){
			i = i+v;
		}
		
		return i/(double) vector.length;
		
		
	}
	public  List<InfoTerm> printPath(InfoTerm target){
		List<InfoTerm> path = new ArrayList<InfoTerm>();

		for(InfoTerm InfoTerm = target; InfoTerm!=this; InfoTerm = InfoTerm.next){
			path.add(InfoTerm);
		}
		path.add(this); // we add the source too
		Collections.reverse(path);

		return path;
	}
	
//	public InfoTerm(InfoTerm ti, Set<String> desc,Set<String> chil){
//		
//		this.h_scores = ti.h_scores;
//		this.id = ti.id;
//		this.name = ti.name;
//		this.is_a = new Link(ti.is_a);
//		this.part_of = new Link(part_of);
//		this.proteinsgenome = new HashSet<String>();
//		this.probability = new ArrayList<Double>(ti.probability);
//		this.ICs  = new ArrayList<Double>(ti.ICs);
//		this.distancias = new Hashtable<String,Double>(ti.distancias);
//		this.geneSet = new HashSet<String>(ti.geneSet);
//		this.genome = new HashSet<String>(ti.genome);
//		this.bits = new BitSet();
//		
//	}
//	
	public void Both() {
		
		Set<String> anc = new HashSet<String>();
		anc.addAll(this.part_of.ancestors);
		anc.addAll(this.is_a.ancestors);
		Set<String> des = new HashSet<String>(this.is_a.descendants);
		des.addAll(this.part_of.descendants);
		Set<String> chi = new HashSet<String>(this.is_a.childrens);
		chi.addAll(this.part_of.childrens);
		Set<String> par = new HashSet<String>(this.is_a.parents);
		par.addAll(this.part_of.parents);
		Set<String> lev = new HashSet<String>(this.is_a.descLeaves);
		lev.addAll(this.part_of.descLeaves);
	
		this.both.ancestors.addAll(anc);
		this.both.descendants.addAll(des);
		this.both.childrens.addAll(chi);
		this.both.parents.addAll(par);
		this.both.descLeaves.addAll(lev);
	}
	
	public void addICs(List<Double> ic){
		
		this.ICs.addAll(ic);
		
	}

	

     public void addGen(String g, GlobalOntology go){
    	 
    	 
		
		this.geneSet.add(g);
		
		for(String a : this.is_a.ancestors){
			go.allStringtoInfoTerm.get(a).geneSet.add(g); //addGen(g, go);
		}
		
		
	}
     
     public void addGenPOF(String g, GlobalOntology go){
    	 
    	 
 		
 		this.geneSet.add(g);
 		
 		for(String a : this.part_of.ancestors){
 			go.allStringtoInfoTerm.get(a).geneSet.add(g); //addGen(g, go);
 		}
 		
 		
 	}
     public void addGenBOTH(String g, GlobalOntology go){
    	 
    	 
 		
 		this.geneSet.add(g);
 		
 		for(String a : this.both.ancestors){
 			go.allStringtoInfoTerm.get(a).geneSet.add(g); //addGen(g, go);
 		}
 		
 		
  	}
 	
	
	

	
	
	
//	public  void addPartOfAncestor(Set<String> poft){
//	//	poft.removeAll(this.ancestor);  // lo elimino porque considero que un termino puede ser una cosa y ser parte de esa cosa
//		
//		this.part_of.ancestors.addAll(poft);
//		
//	}
//	public  void addPartOfDescendant(Set<String> poft){
//		//poft.removeAll(this.descendant);
//		
//		this.part_of.descendants.addAll(poft);
//		
//	}
//	public  void addProteinsGenome(Set<String> prot){
//		
//		
//		this.proteinsgenome.addAll(prot);
//		
//	}
   
   
    public String getRegulatesClass(){

    	if(!this.positiveR.isEmpty()) return this.positiveR;
    	else if(!this.negativeR.isEmpty()) return this.negativeR;
    	else if(!this.regulate.isEmpty())  return this.regulate;
    	else return null;
    	
    }
	
	
	public Double depth(){
		return this.h_scores;
	}
	public String toString(){
		return this.id;
	}
	public String toName(){
		return this.name;
	}
	
	public void SaveDistance(Hashtable<String,Double> d){
		this.distancias.putAll(d);
	}
	public void addleaves(String cl){
		this.is_a.descLeaves.add(cl);
	}
	
	@Override
	public int compareTo(InfoTerm o) {
		// TODO Auto-generated method stub
		return this.id.compareTo(o.id);
	 
	}
	
	
	@Override
	public boolean equals(Object obj){
		
		
		boolean res = false;
		if(obj != null && obj instanceof InfoTerm){
			res = this.id == ((InfoTerm) obj).id;
		}
		return res;
	}
	@Override
	public int hashCode(){
		return this.id.hashCode();
	}
	
	
}
