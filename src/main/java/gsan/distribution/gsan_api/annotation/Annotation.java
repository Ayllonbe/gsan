package gsan.distribution.gsan_api.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
@SuppressWarnings("serial")
public class Annotation implements Serializable {
	/**
	 * 
	 */
	//private static final long serialVersionUID = -7495373191580557959L;
//	static long time_start;
//	static long time_end;


	public Map<String,AnnotationProperty> annotation;
	//String organism = "N/A";
	public Set<String> genes;
	//public Map<String,Double> percentile;
	//public Map<String,Map<Integer,Double>> percentileOnt;

	//private final Logger log = LoggerFactory.getLogger(this.getClass());

	
	public Annotation() {
		this.annotation=new HashMap<>();
		this.genes = new HashSet<>();
	}

	public Annotation(Set<String> genes, HashMap<String, AnnotationProperty> newgoa) {
		// TODO Auto-generated constructor stub
		this.annotation = new HashMap<String,AnnotationProperty>(newgoa);

		this.genes = new HashSet<String>(genes);
	//	this.percentile = percentile;
	}

	public Annotation(List<List<String>> table, GlobalOntology gont,boolean bol, int ids){
	//	this.percentile = new HashMap<>();
//		for(String subont : gont.subontology.keySet()) {
//			System.out.println(subont);
//			this.percentile.put(subont, gont.getPercentile(percentile, author, subont));
//		}

		try {
			this.annotation = new HashMap<String, AnnotationProperty>();
			this.genes = new HashSet<String>();
//			int ndNot = 0;
//			int obs = 0;
			List<String> avoidedEC = new LinkedList<String>();
			avoidedEC.add("NOT");
			avoidedEC.add("ND");
			if(!bol) {
				avoidedEC.add("IEA");
			}


			// Recupera toda la anotacion a partir de su fichero
			for(List<String> line:table){
//				System.out.println(line);
				//			log.debug(line);
				String gene = line.get(ids).toLowerCase();
				List<String> synonyms = Arrays.asList(line.get(10).toLowerCase().split("[|]"));
				if(!gene.equals("") &&!(avoidedEC.contains(line.get(3))||avoidedEC.contains(line.get(6)))){  // 
					//if(!(line.get(3).equals("NOT")|| line.get(6).equals("ND")  || line.get(6).equals("IEA") )){
					String term = line.get(4);
					//String ont = line.get(8);
					//	String name = line.get(6);
					if(!this.annotation.containsKey(gene)) {
						this.annotation.put(gene, new AnnotationProperty(gene));
						this.annotation.get(gene).setSymbol(gene);

					}
					for(String syn : synonyms) {
						if(!this.annotation.containsKey(syn)) {
							this.annotation.put(syn, new AnnotationProperty(syn));
							this.annotation.get(syn).setSymbol(syn);

						}
					}
					if(gont.allStringtoInfoTerm.containsKey(term)) {

						if(gont.allStringtoInfoTerm.get(term).getRegulatesClass()!=null) {
							String regTerm = gont.allStringtoInfoTerm.get(term).getRegulatesClass();
							InfoTerm it_reg = gont.allStringtoInfoTerm.get(regTerm);
							this.annotation.get(gene).setTerm(regTerm,it_reg.top);
							for(String syn : synonyms) {
									this.annotation.get(syn).setTerm(regTerm,it_reg.top);
							}
						}else {
							InfoTerm it= gont.allStringtoInfoTerm.get(term);
							this.annotation.get(gene).setTerm(term, it.top);
							for(String syn : synonyms) {
								this.annotation.get(syn).setTerm(term,it.top);
						}

						}

					}





				}
//				else { ndNot ++;}

			}
			this.genes.addAll(this.annotation.keySet());
//			log.debug("NDNOT number " + ndNot);
//			log.debug("Obsolete number " + obs);

			for(String gen : this.genes) {
				this.annotation.get(gen).idf = Math.log10((double)genes.size()/(double)this.annotation.get(gen).getTerms().size())/ Math.log10((double)genes.size());
			}
		}catch(Exception e) {
//			log.error(e.getLocalizedMessage());
		}

	}

	public static Annotation redondancyReduction(Annotation goa,GlobalOntology go) {

		try {


			HashMap<String, AnnotationProperty> newgoa = new HashMap<String,AnnotationProperty>(); 

			for(String g : goa.annotation.keySet()) {
				newgoa.put(g, new AnnotationProperty(g));
				for(String s : go.subontology.keySet()) {
					List <String> termOnt = new ArrayList<String>(goa.annotation.get(g).getTerms(s));
					for(int i = 0; i<termOnt.size();i ++) {
						InfoTerm t1 = go.allStringtoInfoTerm.get(termOnt.get(i));

						for(int j= i+1; j<termOnt.size();j++) {
							InfoTerm t2 = go.allStringtoInfoTerm.get(termOnt.get(j));
							if(t1.is_a.descendants.contains(t2.toString())){
								termOnt.remove(i);
								i--;
								break;
							}else if(t2.is_a.descendants.contains(t1.toString())){
								termOnt.remove(j);
								j--;

							}

						}

					}
					newgoa.get(g).setAllTerms(new HashSet<String>(termOnt),s);


				}

				newgoa.get(g).name=goa.annotation.get(g).name;
				newgoa.get(g).setSymbol(goa.annotation.get(g).getSymbol());
				newgoa.get(g).idf=goa.annotation.get(g).idf;
			}

			Annotation GOAred = new Annotation(goa.genes,newgoa);

			//	GOAred.percentileOnt.putAll(goa.percentileOnt);





			return GOAred;
		}catch(Exception e) {

//			goa.log.error(e.getLocalizedMessage());
			return null;
		}





	}


	public static Annotation icIncompleteReduction(Annotation goa,GlobalOntology go, int ic, Map<String,Double> percentile) {

		try {

			HashMap<String, AnnotationProperty> newgoa = new HashMap<String,AnnotationProperty>(); 
			Set<String> genes = new HashSet<String>();
			for(String g : goa.annotation.keySet()) {
				newgoa.put(g, new AnnotationProperty(g));
				for(String s : go.subontology.keySet()) {
					List <String> termOnt = new ArrayList<String>(goa.annotation.get(g).getTerms(s));
					for(int i = 0; i<termOnt.size();i ++) {
						InfoTerm t1 = go.allStringtoInfoTerm.get(termOnt.get(i));
						//log.debug(t1.top +" " +t1.ICs.get(ic) + " " +ic);
						if(t1.ICs.get(ic)<percentile.get(t1.ontology)) {
							//					if((t1.ICs.get(ic)/go.top2MaxIC.get(t1.top).get(ic))<goa.percentileOnt.get(t1.top)) {
							termOnt.remove(i);
							i--;
						}


					}
					newgoa.get(g).setAllTerms(new HashSet<String>(termOnt),s);


				}

				genes.add(g);
				newgoa.get(g).name=goa.annotation.get(g).name;
				newgoa.get(g).setSymbol(goa.annotation.get(g).getSymbol());
				newgoa.get(g).idf=goa.annotation.get(g).idf;

			}

			Annotation GOAred = new Annotation(genes, newgoa);

			List<String> gremove = new ArrayList<>();
			for(String g : GOAred.annotation.keySet()) {
				if(GOAred.annotation.get(g).getTerms().isEmpty()) {
					gremove.add(g);
				}
			}

			for(String g : gremove )GOAred.annotation.remove(g);

			GOAred.genes.removeAll(gremove);


			return GOAred;

		}catch(Exception e) {
//			goa.log.error(e.getLocalizedMessage());
			return null;
		}




	}





	public  List<String> getTermsRR(HashMap<String,List<String>> g2term){

		Set<String> newlistterm = new HashSet<String>();
		for(String p:g2term.keySet()){
			newlistterm.addAll(g2term.get(p));
		}
		return new ArrayList<String>(newlistterm);
	}

	public  List<String> getTerms(String ontology){

		Set<String> newlistterm = new HashSet<String>();
		for(String p:this.annotation.keySet()){
			newlistterm.addAll(this.annotation.get(p.toLowerCase()).getTerms(ontology));
		}
		return new ArrayList<String>(newlistterm);
	}

	public  List<String> getAnnotation(String ontology){ // Sans filtrer

		List<String> newlistterm = new ArrayList<String>();
		for(String p:this.annotation.keySet()){
			newlistterm.addAll(this.annotation.get(p.toLowerCase()).getTerms(ontology));
		}
		return newlistterm;
	}
	public  List<String> getAnnotation(List<String> genes,String ontology){ // Sans filtrer

		List<String> newlistterm = new ArrayList<String>();
		for(String p:new HashSet<String>(genes)){
			if(this.annotation.containsKey(p)) {
				newlistterm.addAll(this.annotation.get(p.toLowerCase()).getTerms(ontology));
			}
		}
		return newlistterm;
	}
	public  List<String> getTerms(List<String> genes, String top, GlobalOntology go){

		Set<String> newlistterm = new HashSet<String>();
		Set<String> genesNoNoted = new HashSet<>();

		for(String p:new HashSet<String>(genes)){
			if(!this.annotation.containsKey(p.toLowerCase())) {
				genesNoNoted.add(p);
				//log.debug(p);
			}else {

				List<String> termes = this.annotation.get(p.toLowerCase()).getTerms(top);

				if(termes.isEmpty()) {
					genesNoNoted.add(p);
				}
				//log.debug(p + " " + ontology.top);
				newlistterm.addAll(termes);
				//			for(String nelt : termes) {
				//				sb.append(p + "\t" +ontology.stringtoInfoTerm.get(nelt).toString()+"\t"+ ontology.stringtoInfoTerm.get(nelt).toName()+"\n");
				//			}
				//log.debug(this.annotation.get(p).bp);
				for(String t : termes) {
					InfoTerm it = go.allStringtoInfoTerm.get(t);
					it.addGen(p,go);
					//				if(it.getRegulatesClass()!=null) {
					//					go.allStringtoInfoTerm.get(it.getRegulatesClass()).addGen(p, go);
					//				}
				}
			}
		}

		//		try {
		//			PrintWriter pw = new PrintWriter("/home/aaron/Bureau/"+ontology.top+".csv");
		//			pw.debug(sb);
		//			pw.close();
		//		} catch (FileNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//log.debug(new HashSet<String>(genes).size());
		//System.out.println(genesNoNoted.size() + " " +top);
//		log.debug("There are "+genesNoNoted.size()+" genes no registred in GOA for " + go.allStringtoInfoTerm.get(top).toName());
//		log.debug("There are "+(genes.size()-genesNoNoted.size())+" genes registred in GOA for " + go.allStringtoInfoTerm.get(top).toName());


		return new ArrayList<String>(newlistterm);
	}
	public  List<String> getTermsSansPrint(List<String> genes, String top, GlobalOntology go){

		Set<String> newlistterm = new HashSet<String>();
		Set<String> genesNoNoted = new HashSet<>();

		for(String p:new HashSet<String>(genes)){
			if(!this.annotation.containsKey(p)) {
				genesNoNoted.add(p);
				//log.debug(p);
			}else {

				List<String> termes = this.annotation.get(p.toLowerCase()).getTerms(top);

				if(termes.isEmpty()) {
					genesNoNoted.add(p);
				}
				//log.debug(p + " " + ontology.top);
				newlistterm.addAll(termes);
				//		for(String nelt : termes) {
				//			sb.append(p + "\t" +ontology.stringtoInfoTerm.get(nelt).toString()+"\t"+ ontology.stringtoInfoTerm.get(nelt).toName()+"\n");
				//		}
				//log.debug(this.annotation.get(p).bp);
				for(String t : termes) {
					InfoTerm it = go.allStringtoInfoTerm.get(t);
					it.addGen(p,go);
					//			if(it.getRegulatesClass()!=null) {
					//				go.allStringtoInfoTerm.get(it.getRegulatesClass()).addGen(p, go);
					//			}
				}
			}
		}

		//	try {
		//		PrintWriter pw = new PrintWriter("/home/aaron/Bureau/"+ontology.top+".csv");
		//		pw.debug(sb);
		//		pw.close();
		//	} catch (FileNotFoundException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
		//log.debug(new HashSet<String>(genes).size());

		return new ArrayList<String>(newlistterm);
	}
	public  List<String> getTerms(List<String> genes, String ontology){

		Set<String> newlistterm = new HashSet<String>();
//		int count = 0;
		for(String p:genes){
			if(!this.annotation.containsKey(p)) {
//				count ++ ;
			}else {
				newlistterm.addAll(this.annotation.get(p.toLowerCase()).getTerms(ontology));

			}
		}
//		log.debug("There are "+count+" genes no registred in GOA for " + ontology);
		return new ArrayList<String>(newlistterm);
	}

	public AnnotationProperty get(String prote){
		return this.annotation.get(prote.toLowerCase());

	}
	public boolean containsKey(String prote){
		return this.annotation.containsKey(prote);

	}
	public Integer size(){
		return this.annotation.size();

	}
}

