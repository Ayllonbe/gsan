package gsan.distribution.gsan_api.run.representative;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class AlgorithmRepresentative {

	public final int ic_inc;
	public final String ontologies;
	public final Map<String, Object>fileSS;
	public final String mhcl;
	public final double tailmin;
	public final double filtre;
	public final double precision;
	public final int compare;
	Communication com;
	public int errorMsg = 0;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public AlgorithmRepresentative(int ic,String ont,Map<String, Object> mSS, String mhcl,
			double tailmin,double filtre, double precision,int compare) {

		this.ic_inc = ic;
		this.ontologies = ont;
		this.fileSS = mSS;
		this.mhcl = mhcl;
		this.tailmin = tailmin;
		this.filtre = filtre;
		this.precision = precision;
		this.compare = compare;
		log.debug("Computing hierarchical clustering");
		this.com = new Communication();
		this.chargeCom();


	}

	public AlgorithmRepresentative(int ic,String ont, Map<String, Object> file, 
			double tailmin,double filtre, double precision,int compare) {

		this.ic_inc = ic;
		this.ontologies = ont;
		this.tailmin = tailmin;
		this.fileSS = null;
		this.mhcl = null;
		this.filtre = filtre;
		this.precision = precision;
		this.compare = compare;
	

	}

	private void chargeCom() {
//		System.out.println("REPRESENTATIVE");
		com = Communication.comunication(fileSS, mhcl);
//		System.out.println(com.clusters);
		this.errorMsg = com.error;
	}

	public List<Cluster> run(GlobalOntology go, List<String>terminos,Annotation GOA,Map<String, Double> percentile) {

		try {


//			for(Integer i : com.clusters.keySet()) {
//				System.out.println("Cluster " + i);
//				for(String s : com.clusters.get(i)) {
//					System.out.println("\t" + go.allStringtoInfoTerm.get(s).toName());
//				}
//			}

			Map<Integer,List<Representative>> cl2rep = getRepresentative(go,ontologies,ic_inc,  com.clusters, 
					tailmin, filtre, precision,compare);

			Map<Integer,Integer> cl2nbg = new HashMap<>();
			for(Entry<Integer,List<Representative>> i : cl2rep.entrySet()){

				for(Representative rep : cl2rep.get(i.getKey())){

					InfoTerm it = rep.repesentative;
//					System.out.println(it.toName() + " " + it.ICs.get(3)+" "+it.geneSet.size());
					cl2nbg.put(i.getKey(), it.geneSet.size());

				}
			}

			//Transfer as List and sort it

			//Transfer as List and sort it

			ArrayList<Map.Entry<Integer, Integer>> listCluster = new ArrayList<>(cl2nbg.entrySet());
			Collections.sort(listCluster, new Comparator<Map.Entry<?, Integer>>(){

				public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}});

			List<Cluster> clusterList = new LinkedList<>();
			Set<String> genesObserve = new HashSet<>();


			for(Entry<Integer, Integer> entry : listCluster){
				int i = entry.getKey();

				for(Representative rep : cl2rep.get(i)){
					Cluster cl = new Cluster();
					InfoTerm it = rep.repesentative;
					Set<String> gen = new HashSet<>();
					for(InfoTerm s : rep.terms) {
						cl.terms.add(s);
						gen.addAll(s.geneSet);	
					}




					if(!it.termcombi.isEmpty()) {
						for(String ti : it.termcombi) { 

							InfoTerm cIT = go.allStringtoInfoTerm.get(ti);

							Set<String> retGen = new HashSet<>(cIT.geneSet);
							retGen.retainAll(gen);

							if(cIT.ICs.get(ic_inc)>percentile.get(cIT.ontology)&&retGen.size()>compare) {
								cl.representatives.add(cIT);
								genesObserve.addAll(cIT.geneSet);
								clusterList.add(cl);
							}
						}
					}else {
						if(it.ICs.get(ic_inc)>percentile.get(it.ontology)&&gen.size()>compare) {
//							System.out.println("Cluster "+it.toName()+" "+it.ICs.get(ic_inc) +" "+it.geneSet);
							cl.representatives.add(it);
							genesObserve.addAll(it.geneSet);
							clusterList.add(cl);
						}
					}



				}

			}
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics statsDepth = new DescriptiveStatistics();
			DescriptiveStatistics statsICSeco = new DescriptiveStatistics();

			// Add the data from the array
			for(Cluster cl : clusterList) {
				for(InfoTerm rep : cl.representatives) {
					statsDepth.addValue(rep.depth());
					statsICSeco.addValue(rep.ICs.get(0));
				}
			}

			return clusterList;

		} catch(Exception e){
			if(this.errorMsg>0) {
				this.errorMsg = 4;
			}
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
			return null;
		}


	}

	public Map<Integer, List<Representative>> getRepresentative(GlobalOntology go,String sub,int ic,
			Hashtable<Integer,List<String>> clusters,double tailmin,double RepCombinedSimilarity, double precision,double compare) throws Exception
	{
		log.debug("Computing Representative Algorithm...");
		// Default

		/*
		 * Al final de este procedimiento, obtendremos un hashtable donde tenemos para cada custer una lista de terminos
		 * representativos para ese cluster.
		 *  
		 */
		double tolerance = 0.7;
		Hashtable<Integer, List<Representative>> clu2rep = new Hashtable<Integer, List<Representative> >();
//		int MaxCombination = 0;
//		int termClusterMax = 0;
		for(Integer cl : clusters.keySet()){ // Para cada cluster
			List<String> termCluster = clusters.get(cl);

			Set<String> genes = new HashSet<>();
			for(String term : termCluster) {
				genes.addAll(go.allStringtoInfoTerm.get(term).geneSet);


			}
			List<InfoTerm> candidate = new ArrayList<>();

			List<Double> ics_list = new ArrayList<>();

			List<String> st = new ArrayList<String>();

			for(String term : termCluster) {

				if(((double)go.allStringtoInfoTerm.get(term).geneSet.size()/(double)genes.size())>=tolerance&&go.allStringtoInfoTerm.get(term).geneSet.size()>=compare) { 

					st.add(term);
				}

			}

		  
			if(st.size()>0) {
				for(int i = 0; i < st.size();i++) {
					InfoTerm r1 = go.allStringtoInfoTerm.get(st.get(i));
					for(int z=i+1; z<st.size();z++) {
						InfoTerm r2 = go.allStringtoInfoTerm.get(st.get(z));
						if(r1.is_a.descendants.contains(r2.id)) {
							st.remove(i);
							i--;
							break;
						}else if(r1.is_a.ancestors.contains(r2.id)) {
							st.remove(z);
							z--;
						}
					}
					
					
				}
				
			}

			
			if(st.size()==1) {
				
					InfoTerm res = go.allStringtoInfoTerm.get(st.get(0));
					ics_list.add(res.ICs.get(ic));
					candidate.add(res);
				
				
			}else {
			int ncombi = termCluster.size()>1?((int) Math.floor(Math.sqrt(Math.abs(termCluster.size()/10)-1))+2):1 ; // numero que indica el numero de combinaciones deseadas
				ncombi = ncombi >6? 6:ncombi;
			Set<InfoTerm> representatives = new HashSet<>(getManyRep(termCluster,sub,go,ncombi,tailmin,RepCombinedSimilarity,precision,ic));
			for(InfoTerm res:representatives) {
				ics_list.add(res.ICs.get(ic));
				candidate.add(res);
			}
			}	
			
			
			
			
			if(candidate.size()>0) {
				InfoTerm it = candidate.get(ics_list.indexOf(Collections.max(ics_list)));
				Set<InfoTerm> infoterm = new HashSet<>();
				for(String t : termCluster){
					/*
					 * Only keep the descendant terms of the representative. 
					 */
					if(it.toString().equals(t)  || it.is_a.descendants.contains(t)) { 
						infoterm.add(go.allStringtoInfoTerm.get(t));
					}

				}
				Representative rep = new Representative(it.toString(),it,infoterm);
				clu2rep.put(cl, new ArrayList<>());
				clu2rep.get(cl).add(rep);
			}


		}

		




//		System.out.println("Max combination obtained is: " + MaxCombination);
//		System.out.println("Max number of terms in a cluster is: " + termClusterMax);

		return clu2rep;
	}

	public static Set<InfoTerm> getManyRep(List<String> termList,String topSubOnt,GlobalOntology go,int ncombi,double tailmin,double RepCombinedSimilarity,double precision,int ic) throws Exception{

		Stack<String> stack = new Stack<String>();
		/*
		 *  Initialize stack and bitset.
		 */
		for(int i = 0; i<termList.size();i++){
			String St = termList.get(i);
			stack.push(St);
			InfoTerm term = go.allStringtoInfoTerm.get(St); 
			term.bits.set(i);
		}
		/*
		 * Charge the set of terms and their ancestors.
		 */
		Set<String> termSubGraph = new HashSet<String>(termList);

		for(String t : termList){
			termSubGraph.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);


		}

		/*
		 * Using the true path to heritage the bitset information from children to parent.
		 */
		while(stack.size()>0){
			String x = (String) stack.pop();
			InfoTerm term = go.allStringtoInfoTerm.get(x);
			for(String p : term.is_a.parents){
				InfoTerm parent = go.allStringtoInfoTerm.get(p);
				BitSet pivot = new BitSet();
				pivot.or(term.bits);
				pivot.andNot(parent.bits);
				if(!pivot.isEmpty()){
					parent.bits.or(term.bits);
					stack.push(p);
				}
			}


		}

		/*
		 * Get the most specific representative terms ancestor of every term in a given cluster
		 */
		Set<InfoTerm> candidates =  getOneRep(termSubGraph,topSubOnt, go,termList.size(),precision); 
		Set<InfoTerm> candidateManyRep = new HashSet<InfoTerm>();

		int limit = 2;
		while(limit<=ncombi){


			for(InfoTerm repCan : candidates){
				List<String> consideredChildren = new ArrayList<String>();

				for(String d : repCan.is_a.childrens){ /// ATENTION CHANGEMENT
					if(termSubGraph.contains(d)){
						if((double)go.allStringtoInfoTerm.get(d).bits.cardinality()/(double)termList.size()>=tailmin)

							consideredChildren.add(d);
					}
				}
				candidateManyRep = new HashSet<InfoTerm>();

				Set<Set<String>> dC  = doCombine(repCan, consideredChildren,termList.size(), go,   RepCombinedSimilarity,limit,precision); // TEST				// Get the most specific terms of the combination
				//System.out.println(dC);
				for(Set<String> sdc : dC){

					List<List<InfoTerm>> li = new ArrayList<List<InfoTerm>>();
					for(String t : sdc){
						InfoTerm term = go.allStringtoInfoTerm.get(t);
						Set<InfoTerm> X = getSpeficicTerm(term.toString(),termSubGraph,go);
						li.add(new ArrayList<InfoTerm>(X));
					}
					Set<InfoTerm> result = new HashSet<InfoTerm>();
					List<InfoTerm> current = new ArrayList<InfoTerm>();
					candidateManyRep.addAll(GeneratePermutations(li, result, 0, current,go));
				}

			}
			if(!candidateManyRep.isEmpty()){
				candidates.clear();

				List<Double> list_ic = new ArrayList<Double>();
				for(InfoTerm t : candidateManyRep ){
					list_ic.add( t.ICs.get(ic));


				}
				double ICmax = Collections.max(list_ic);

				for(InfoTerm t : candidateManyRep){
					if(t.ICs.get(ic) == ICmax){
						candidates.add(t);
					}
				}
				candidateManyRep.clear();
			}
			limit++;

		}

		for(String d : termSubGraph) go.allStringtoInfoTerm.get(d).bits.clear();
		return candidates;
	}

	public static BitSet compare(BitSet lhs, BitSet rhs){
		if(lhs.equals(rhs)) return lhs;
		return lhs.cardinality()<rhs.cardinality()?lhs:rhs;

	}

	public static Set<InfoTerm> getOneRep(Set<String> termSubGraph,String topSubOnt,GlobalOntology go, int termsize,double precision){
		Stack<String> stack = new Stack<String>(); 
		stack.push(topSubOnt);
		Set<InfoTerm> candidateSet = new HashSet<InfoTerm>();
		while(stack.size()>0){
			String t = stack.pop();
			InfoTerm term = go.allStringtoInfoTerm.get(t); 
			int count = 0;
			for(String e :  term.is_a.childrens){ // Pour chaque fils de ta
				if(termSubGraph.contains(e)){
					InfoTerm termChildren = go.allStringtoInfoTerm.get(e);
					//		if(ta.bits.equals(tx.bits)){
					if((double)termChildren.bits.cardinality()/(double)termsize>=precision){
						stack.push(e);
						count++;
					}
				}}
			if(count==0){
				candidateSet.add(term);
			}
		}
		return candidateSet;

	}

	public static Set<InfoTerm> getSpeficicTerm(String top,Set<String> termSubGraph,GlobalOntology go){
		Stack<String> stack = new Stack<String>(); 
		stack.push(top);
		Set<InfoTerm> candidateSet = new HashSet<InfoTerm>();
		while(stack.size()>0){
			String t = stack.pop();
			InfoTerm term = go.allStringtoInfoTerm.get(t); 
			int count = 0;
			for(String e :  term.is_a.childrens){ // Pour chaque fils de ta
				if(termSubGraph.contains(e)){
					InfoTerm termChildren = go.allStringtoInfoTerm.get(e);
					if(term.bits.equals(termChildren.bits)){ // Filtro invariable! 
						stack.push(e);
						count++;
					}
				}}
			if(count==0){

				candidateSet.add(term);
			}
		}
		return candidateSet;

	}

	private static Set<Set<String>> doCombine(InfoTerm ta,List<String> children, int termsize,GlobalOntology go, double RepCombinedSimilarity,int ncombi,double precision) {


		Set<List<String>> combinationSet = Combination.ncombination(children ,ncombi);
		//System.out.println(combinationSet);
		Set<Set<String>> goodCombinedSet = new HashSet<Set<String>>();
		for(List<String> ns : combinationSet) {

			BitSet bitset = new BitSet();
			int control = 0;
			for(int i = 0;i<ns.size();i++){
				InfoTerm tx = go.allStringtoInfoTerm.get(ns.get(i));

				for(int j = i+1;j<ns.size();j++){
					InfoTerm ty = go.allStringtoInfoTerm.get(ns.get(j));
					BitSet inter = new BitSet();
					inter.or(tx.bits);
					inter.and(ty.bits);

					BitSet union = new BitSet();
					union.or(tx.bits);
					union.or(ty.bits);

					double jaccard = ((double)inter.cardinality())/(double)union.cardinality();

					if(jaccard>RepCombinedSimilarity){ // el simbolo ">" es porque vamos a romper el bucle si se da el caso. 
						//					// El RepCombinedSimilarity es para evitar tener una combinacion de dos elementos similares.
						control++;	
						break;	
					}	

				}
				if(control!=0){ break;}
				else{
					bitset.or(tx.bits);
				}
			}

			if(control==0&&((double)bitset.cardinality()/(double)termsize)>=precision){ // Elegir cuanta precision queremos en el resultado.

				Collections.sort(ns);
				goodCombinedSet.add(new HashSet<String>(ns));
			}



		}
		return goodCombinedSet;
	}

	public static  Set<InfoTerm> GeneratePermutations(List<List<InfoTerm>> Lists, Set<InfoTerm> result, int depth, List<InfoTerm> current, GlobalOntology go) throws Exception
	{
		current = new ArrayList<InfoTerm>(current);


		if(depth == Lists.size())
		{
			Collections.sort(current);


			InfoTerm tf = new InfoTerm(current);
			if(!result.contains(tf)){
				result.add(tf);}
			return result;
		}

		for(int i = 0; i < Lists.get(depth).size(); ++i)
		{
			current.add(Lists.get(depth).get(i));
			GeneratePermutations(Lists, result, depth + 1, current,go);
			current.remove(current.size()-1);
		}


		return result;
	}

}
