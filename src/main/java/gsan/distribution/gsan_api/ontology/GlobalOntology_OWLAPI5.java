//package gsan.distribution.gsan_api.ontology;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URLDecoder;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.TreeSet;
//import java.util.stream.Collectors;
//
//import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
//import org.semanticweb.elk.owlapi.ElkReasonerFactory;
//import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.model.IRI;
//import org.semanticweb.owlapi.model.OWLAnnotation;
//import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
//import org.semanticweb.owlapi.model.OWLAnnotationProperty;
//import org.semanticweb.owlapi.model.OWLClass;
//import org.semanticweb.owlapi.model.OWLDataFactory;
//import org.semanticweb.owlapi.model.OWLException;
//import org.semanticweb.owlapi.model.OWLLiteral;
//import org.semanticweb.owlapi.model.OWLObjectProperty;
//import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.model.OWLOntologyManager;
//import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
//import org.semanticweb.owlapi.reasoner.OWLReasoner;
//import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
//import org.semanticweb.owlapi.search.EntitySearcher;
//import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import gsan.distribution.algorihms.astar.AstarSearchAlgo;
//import gsan.distribution.algorihms.astar.Edges;
//import gsan.distribution.gsan_api.annotation.Annotation;
//public class GlobalOntology_OWLAPI5 {
//
//	public Hashtable<String,InfoTerm> allStringtoInfoTerm = new Hashtable<String,InfoTerm>();
//	public Hashtable<String, List<String>> obsolete2consORrepl = new Hashtable<String,List<String>>();
//	public Map<String,OntoInfo> subontology = new HashMap<>();
//	public Set<String> sourceSet = new HashSet<>();
//	public final String owlprefix = "http://purl.obolibrary.org/obo/";
//	private static  OWLReasonerFactory reasonerFactory;
//	public static OWLOntology ontology;
//	private OWLReasoner reasoner ;
//	private final Logger log = LoggerFactory.getLogger(this.getClass());
//	private static OWLOntologyManager manager;
//	public Map<String,Map<String,DescriptiveStatistics>> IC2DS;
//	public Set<String> termsProks = new HashSet<>();
//	
//	
//
//
//	//private Map<String,Set<String>> go2descendantORG = new HashMap<>();
//
//	@SuppressWarnings("static-access")
//	public GlobalOntology_OWLAPI5(GlobalOntology_OWLAPI5 go) {
//		this.ontology = go.ontology;
//		this.reasonerFactory = go.reasonerFactory;
//		this.manager = go.manager;
//		this.sourceSet = new HashSet<>(go.sourceSet);
//		//System.out.println(this.reasoner);
//		this.reasoner = go.reasoner;
//		this.termsProks = new HashSet<>(go.termsProks);
//		this.allStringtoInfoTerm = new Hashtable<>();
//		this.IC2DS = new HashMap<>(go.IC2DS);
//		for(String t : go.allStringtoInfoTerm.keySet()) {
//			this.allStringtoInfoTerm.put(t,new InfoTerm(go.allStringtoInfoTerm.get(t)));
//		}
//		this.obsolete2consORrepl = new Hashtable<>(go.obsolete2consORrepl);
//		
//		for(Entry<String,OntoInfo> entry : go.subontology.entrySet()) {
//			this.subontology.put(entry.getKey(), new OntoInfo(entry.getValue()));
//		}
//		
//				
//	}
//
//	@SuppressWarnings("static-access")
//	public GlobalOntology_OWLAPI5(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory,String source)
//			throws OWLException, MalformedURLException {
//		this.manager = manager;
//		this.reasonerFactory = reasonerFactory;
//		this.IC2DS = new HashMap<>();
//		this.sourceSet.add(source);
//	}
//	public GlobalOntology_OWLAPI5(Map<String,String> info, Map<String,Set<String>> p2c,Map<String,Set<String>> c2p, String source){
//		this.allStringtoInfoTerm = new Hashtable<>();
//		this.subontology = new HashMap<>();
//		this.sourceSet.add(source);
//		this.IC2DS = new HashMap<>();
//		int flag=1; // Parameters to break the follow while 
//		int level = 0;  // integer showing the depth of a term
//		String top = "reac"; // get the top GO ID
//		this.subontology.put(top,new OntoInfo()); // put the GO id in the global list 
//		TreeSet<String> list2 = new TreeSet<>(); //Pivote set
//		Set<String> list1=new HashSet<>(); // Owl class set to get every information
//		list1.add(top); //we add the subontology owl class
//
//		Set<String> leavesISA = new HashSet<>();
//		while(flag==1) 
//		{
//			for (String pere : list1) { // For each owl class in list1
//
//				InfoTerm iT = new InfoTerm(pere,level,source); // Create InfoTerm class to this owl class
//				iT.name = info.get(pere); // get GO name and add in InfoTerm class
//				iT.top = top; // get the top of subOntology and add in InfoTerm class
//
//				/*
//				 * The follow 4 lines is to add in a Link class the is a taxonomy
//				 * The method changetype is to transform the owl class to GO id
//				 */
//				iT.is_a.ancestors.addAll(this.getAllHierarchy(pere, c2p));
//				iT.is_a.parents.addAll(c2p.get(pere));
//				iT.is_a.descendants.addAll(this.getAllHierarchy(pere, p2c));
//				iT.is_a.childrens.addAll(p2c.get(pere));
//
//				if(iT.is_a.childrens.isEmpty()) {
//					leavesISA.add(iT.toString());
//				}
//
//				list2.addAll(p2c.get(pere)); // Add in list2 every child in list2
//				/*
//				 * The owl sub class of axiom allow get the information about partOf and regulates. 
//				 */
//
//				//System.out.println("END");
//
//
//				this.allStringtoInfoTerm.put(pere, iT);	// put all recovered information in the HashTable
//			}
//
//			list1.clear(); // clear all information in List1
//
//
//			if(!list2.isEmpty()){
//
//				list1.addAll(list2); // we add all information in list1
//				list2.clear(); // clear all information in list2
//				level = level + 1; // increase the depth
//			}
//			else
//				flag=0;
//		}
//		this.subontology.get(top).setMaxDepth((double)level-1); // get the maximal Depth to subOntology
//		this.subontology.get(top).setnumbernodes(this.allStringtoInfoTerm.get(top).is_a.descendants.size());
//
//		int nisA = 0;
//		int npartOf = 0;
//
//		for(String t : this.allStringtoInfoTerm.get(top).is_a.descendants) {
//			nisA = nisA + this.allStringtoInfoTerm.get(t).is_a.parents.size();
//			npartOf = npartOf + this.allStringtoInfoTerm.get(t).part_of.parents.size();
//
//		}
//		this.subontology.get(top).setnumberedge(nisA, npartOf);
//		this.subontology.get(top).addAllLeavesISA(leavesISA);
//		this.Leaves(); // Get the leaves terms in is a taxonomy
//		this.GetICs(); // Add the Intrinsic IC for every GO term
//		this.goUniverseIC(); // Add the GO Universal IC Mazandu et al 2013.
//		
//		this.GetDistanceTerms(); // Recover all distance between terms in the taxonomy
//		Map<String, DescriptiveStatistics> mapSeco = new HashMap<>();
//		Map<String, DescriptiveStatistics> mapZhou = new HashMap<>();
//		Map<String, DescriptiveStatistics> mapSanchez = new HashMap<>();
//		Map<String, DescriptiveStatistics> mapMaz = new HashMap<>();
//		mapSeco.put(source, new DescriptiveStatistics());
//			mapZhou.put(source, new DescriptiveStatistics());
//			mapSanchez.put(source, new DescriptiveStatistics());
//			mapMaz.put(source, new DescriptiveStatistics());
//	
//		
//		for(String t : this.allStringtoInfoTerm.keySet()){
//			
//			mapSeco.get(source).addValue(this.allStringtoInfoTerm.get(t).ICs.get(0));
//			mapZhou.get(source).addValue(this.allStringtoInfoTerm.get(t).ICs.get(1));
//			mapSanchez.get(source).addValue(this.allStringtoInfoTerm.get(t).ICs.get(2));
//			mapMaz.get(source).addValue(this.allStringtoInfoTerm.get(t).ICs.get(3));
//		}
//		this.IC2DS.put("seco", mapSeco);
//		this.IC2DS.put("zhou", mapZhou);
//		this.IC2DS.put("sanchez", mapSanchez);
//		this.IC2DS.put("mazandu", mapMaz);
//
//	}
//
//
//	public Set<String> getAllHierarchy(String term, Map<String,Set<String>> hierarchy){
//		Set<String> allHierarchy = new HashSet<>();
//
//		for(String h : hierarchy.get(term)) {
//			allHierarchy.add(h);
//			allHierarchy.addAll(this.getAllHierarchy(h, hierarchy));
//		}
//
//		return allHierarchy;
//
//	}
//
//
////	public GlobalOntology()
////			throws Exception {
////	}
//	public Hashtable<String,InfoTerm> GetInfoSubOnt(String Onto){
//
//		Hashtable<String,InfoTerm> lo = new Hashtable<String,InfoTerm>();
//
//		lo.put(Onto,this.allStringtoInfoTerm.get(Onto));
//		for(String d : this.allStringtoInfoTerm.get(Onto).is_a.descendants) {
//			lo.put(d, this.allStringtoInfoTerm.get(d));
//		}
//
//
//		return lo;
//
//
//	}
//	
//	
//	/*
//	 * Print the class hierarchy for the given ontology from this class down, assuming this class is at
//	 * the given level. Makes no attempt to deal sensibly with multiple
//	 * inheritance.
//	 */
//	public void AddTermToGenome(Annotation GOA) {
//
//
//
//		for(String g : GOA.annotation.keySet()) { // For each genes
//			for(String sub :this.subontology.keySet()) { // For each sub-ontology
//				for(String t: GOA.annotation.get(g).getTerms(sub)){//For each term in sub-ontology
//					this.allStringtoInfoTerm.get(t).genome.add(g); // We add the gen to term
//					for(String anc :this.allStringtoInfoTerm.get(t).is_a.ancestors) { //for each term ancestor 
//						this.allStringtoInfoTerm.get(anc).genome.add(g); // We add too the gen
//					}
//					//				if(this.allStringtoInfoTerm.get(t).getRegulatesClass()!=null) {
//					//					String r = this.allStringtoInfoTerm.get(t).getRegulatesClass();
//					//					this.allStringtoInfoTerm.get(r).genome.add(g); // We add the gen to term
//					//					for(String anc :this.allStringtoInfoTerm.get(r).is_a.ancestors) { //for each term ancestor 
//					//						this.allStringtoInfoTerm.get(anc).genome.add(g); // We add too the gen
//					//					}
//					//					for(String anc :this.allStringtoInfoTerm.get(r).part_of.ancestors) { //for each term ancestor 
//					//						this.allStringtoInfoTerm.get(anc).genome.add(g); // We add too the gen
//					//					}
//					//				}
//				}
//			}
//		}
//
//
//
//	}
//
//
//
//
//	public void printHierarchy(OWLOntology ontology, OWLClass clazz, String source) throws Exception {
//
//		reasoner = reasonerFactory.createReasoner(ontology); // Creation of reasoner
//		printHierarchy(reasoner, clazz,source); // Once reasoner charged, go to printHierarchy method
//		/* Now print out any unsatisfiable classes */
//		for (OWLClass cl: ontology.classesInSignature().collect(Collectors.toSet())) {
//			if (!reasoner.isSatisfiable(cl)) {
//				log.info("XXX: Ontology no Satisfiable");
//			}
//		}
//	}
//
//
//	public void Leaves() { 
//
//		// Esta function va a recorrer todos los terminos y va mirar si tiene o no hijos, si no tiene hijos todos sus ancestros lo incluyen en sus leaves
//
//		for(String terms : this.allStringtoInfoTerm.keySet()) {
//
//			InfoTerm iT = this.allStringtoInfoTerm.get(terms);
//			if(iT.is_a.childrens.isEmpty()) {
//				for(String anc : iT.is_a.ancestors) {
//					this.allStringtoInfoTerm.get(anc).is_a.descLeaves.add(terms);
//				}
//			}
//
//		}
//		for(String terms : this.allStringtoInfoTerm.keySet()) {
//
//			InfoTerm iT = this.allStringtoInfoTerm.get(terms);
//			if(iT.part_of.childrens.isEmpty()) {
//				for(String anc : iT.part_of.ancestors) {
//					this.allStringtoInfoTerm.get(anc).part_of.descLeaves.add(terms);
//				}
//			}
//
//		}
//	}
//
//	public void completingThePath() {
//
//		for(String t : this.allStringtoInfoTerm.keySet()) { //for each term in GO
//			
//			/*
//			 * A part of B =>  A.part_of.parents = [B]
//			 * A part of X part of B  => A.part_of.ancestors = [X,B]
//			 * A part of X is a B  => A.part_of.ancestors = [X,B]
//			 */
//			
//			
//			if(this.allStringtoInfoTerm.get(t).is_a.childrens.size()==0) { // If term are no childs
//
//				this.generatingPartOfPath(t); // method to generate part of path from leave. It is a recursive method
//			}
//		}
//
//
//		for(String t : this.allStringtoInfoTerm.keySet()) { 
//			Set<String> set = new HashSet<>();
//			for(String ancPO : this.allStringtoInfoTerm.get(t).part_of.ancestors) {
//				//set.add(ancPO);
//				set.addAll(this.allStringtoInfoTerm.get(ancPO).is_a.ancestors);
//			}
//			set.addAll(this.allStringtoInfoTerm.get(t).part_of.ancestors);
//			this.allStringtoInfoTerm.get(t).part_of.ancestors.clear();
//			this.allStringtoInfoTerm.get(t).part_of.ancestors.addAll(set);
//		}
//
//
//		for(String t : this.allStringtoInfoTerm.keySet()) {
//
//			InfoTerm iT = this.allStringtoInfoTerm.get(t);
//			for(String po : iT.part_of.ancestors) {
//				if(iT.part_of.parents.contains(po)) {
//					this.allStringtoInfoTerm.get(po).part_of.childrens.add(iT.id);
//				}
//				this.allStringtoInfoTerm.get(po).part_of.descendants.add(iT.id);
//			}
//
//
//		}
//
//	}
//	public void generatingPartOfPath(String leave) { //depending of completingThePath()
//		/*
//		 * Este algo genera los ancestros, children y descendientes part of que con el razonador tardariamos siglos.
//		 * Para ello hago un while (similar que para recuperar toda la informacion) haciendo etapas top-down.
//		 * Luego para cada padre part of recuperamos todos los ancestros is a y part of de ese padre y lo incluimos en los
//		 * ancestros del termino en question. Tambien hacemos para cada ancestro is a, incluir en ancestros part of todos los ancestros
//		 * part of de cada ancestro. Para los descendientes lo calculamos a partir de cada elemento de la GO miramos sus ancestros part of y para cada 
//		 * ancestro incluimos a ese ancestro los descendientes. 
//		 */
//
//		InfoTerm iT = this.allStringtoInfoTerm.get(leave);
//		//if(  !iT.part_of.parents.isEmpty()) {
//		Set<String> set = new HashSet<>();
//		for(String anc : iT.is_a.parents) {
//			set.addAll(this.completing(anc));
//		}
//		for(String anc : iT.part_of.parents) {
//			set.add(anc);
//			set.addAll(this.completing(anc));
//		}
//		set.addAll(iT.part_of.ancestors);
//		iT.part_of.ancestors.clear();
//		iT.part_of.ancestors.addAll(set);
//
//
//	}
//	private Set<String> completing(String leave) { // depending of generatingPartOfPath(). Is the recursive method
//		/*
//		 * Este algo genera los ancestros, children y descendientes part of que con el razonador tardariamos siglos.
//		 * Para ello hago un while (similar que para recuperar toda la informacion) haciendo etapas top-down.
//		 * Luego para cada padre part of recuperamos todos los ancestros is a y part of de ese padre y lo incluimos en los
//		 * ancestros del termino en question. Tambien hacemos para cada ancestro is a, incluir en ancestros part of todos los ancestros
//		 * part of de cada ancestro. Para los descendientes lo calculamos a partir de cada elemento de la GO miramos sus ancestros part of y para cada 
//		 * ancestro incluimos a ese ancestro los descendientes. 
//		 */
//
//		InfoTerm iT = this.allStringtoInfoTerm.get(leave);
//		//if(  !iT.part_of.parents.isEmpty()) {
//		Set<String> set = new HashSet<>();
//		for(String anc : iT.is_a.parents) {
//			set.addAll(this.completing(anc));
//		}
//		for(String anc : iT.part_of.parents) {
//			set.add(anc);
//			set.addAll(this.completing(anc));
//		}
//		set.addAll(iT.part_of.ancestors);
//		iT.part_of.ancestors.clear();
//		iT.part_of.ancestors.addAll(set);
//		return set;
//
//	}
//	//	
//	//	
//	/**
//	 * Print the class hierarchy from this class down, assuming this class is at
//	 * the given level. Makes no attempt to deal sensibly with multiple
//	 * inheritance.
//	 * @throws Exception 
//	 */
//	public void printHierarchy(OWLReasoner reasoner, OWLClass clazz, String source)
//			throws Exception {
//		IRI oboinowl = IRI.create(URLDecoder.decode("http://www.geneontology.org/formats/oboInOwl#inSubset", "UTF-8"));
//		OWLDataFactory df = OWLManager.getOWLDataFactory();
//		OWLAnnotationProperty subset = df.getOWLAnnotationProperty(oboinowl);
//		
//		oboinowl = IRI.create(URLDecoder.decode("http://www.geneontology.org/formats/oboInOwl#id", "UTF-8"));
//		OWLAnnotationProperty ids = df.getOWLAnnotationProperty(oboinowl);
//		//OWLAnnotationProperty labels = df.getRDFSLabel();
//		
//		
//		
//		oboinowl = IRI.create(URLDecoder.decode("http://www.w3.org/2002/07/owl#deprecated", "UTF-8"));
//		OWLAnnotationProperty deprecated = df.getOWLAnnotationProperty(oboinowl);
//		
//		
//		
//
//		Set<OWLClass> onto = reasoner.getSubClasses(clazz, true).entities().collect(Collectors.toSet()); // Set of OWLClass childs of owl:Thing
//		Set<OWLClass> GOThing = new HashSet<OWLClass>(); // Set of  subOntologies top owlclass
//		Set<OWLClass> GOObsolete = new HashSet<OWLClass>(); // Set of obsolete terms
//
//		for(OWLClass r:onto){ // For each owlclass child of owl:Thing
//
//			boolean sino = false; // boolean to ask if the terms is despreciated 
//			for(OWLAnnotation oa : EntitySearcher.getAnnotations(r.getIRI(), ontology,deprecated).collect(Collectors.toSet()))
//				sino = oa.isDeprecatedIRIAnnotation();
//			
//			if(sino == false){ // False : subontology
//				
//				/*
//				 * That condition is to remove the top of DO without label.
//				 */
//				if(EntitySearcher.getAnnotations(r.getIRI(), ontology,df.getRDFSLabel()).collect(Collectors.toSet()).size()>0){
//					
//					GOThing.add(r);
//				}
//				
//			}
//			else { // True : Obsolete
//				GOObsolete.add(r);
//
//
//			}
//
//		}
//
//		for(OWLClass sub : GOThing){ // For each SubOntology
//			int flag=1; // Parameters to break the follow while 
//			int level = 0;  // integer showing the depth of a term
//			String top = new String(); // get the top GO ID
//			EntitySearcher.getAnnotations(sub.getIRI(), ontology,ids);
//			for(OWLAnnotation oa : EntitySearcher.getAnnotations(sub.getIRI(), ontology,ids).collect(Collectors.toSet()))
//				top = ((OWLLiteral)oa.getValue()).getLiteral();
//			
//			this.subontology.put(top,new OntoInfo()); // put the GO id in the global list 
//			Hashtable<String,InfoTerm> info =new Hashtable<String, InfoTerm>(); // Creation of a HashTable of String to InfoTerm class
//			TreeSet<OWLClass> list2 = new TreeSet<OWLClass>(); //Pivote set
//			Set<OWLClass> list1=new HashSet<OWLClass>(); // Owl class set to get every information
//			list1.add(sub); //we add the subontology owl class
//
//			/*
//			 * In this loop we explore every GO term step by step to compute the depth, the ID, name, the is a taxonomie, the direct part of and regulate.
//			 * After that, the InfoTerm is created and put in the HashTable. The childs of the depth are pushed in list2 and when every terms in List1 is 
//			 * visited, we clear list1 and pushing every terms in list2 to list1, after we clear list2. When list2 is empty we break the while. 
//			 */
//			
//			
//			Set<String> leavesISA = new HashSet<>();
//			while(flag==1) 
//			{
//				for (OWLClass pere : list1) { // For each owl class in list1
//					if (!pere.isBottomEntity() ) { // if that owl class is not a owl:Nothing
//						String id = new String(); // get the GO ID
//						for(OWLAnnotation oa : EntitySearcher.getAnnotations(pere.getIRI(), ontology,ids).collect(Collectors.toSet()))
//							id = ((OWLLiteral)oa.getValue()).getLiteral();
//						boolean prok = false;
//						for(OWLAnnotation oa : EntitySearcher.getAnnotations(pere.getIRI(), ontology,subset).collect(Collectors.toSet()))
//						{
//						
//						String t = oa.getValue().toString().split("#")[1];
//						
//						if(t.equals("gosubset_prok")|| pere.equals(sub)) {
//							prok = true;
//							this.termsProks.add(id);
//						}
//							
//						}
//						
//						InfoTerm iT = new InfoTerm(id,level,prok,source); // Create InfoTerm class to this owl class
//						iT.name = new String(); // get GO name and add in InfoTerm class
//		
//						List<String> labelaxioms = new ArrayList<String>();
//						for(OWLAnnotationAssertionAxiom oa : EntitySearcher.getAnnotationAssertionAxioms(pere.getIRI(), ontology).collect(Collectors.toSet()))
//							{
//							for(OWLAnnotation o : oa.annotations(df.getRDFSLabel()).collect(Collectors.toSet())){
//								labelaxioms.add(((OWLLiteral)o.getValue()).getLiteral());
//							}
//								
//							}
//						for(OWLAnnotation oa : EntitySearcher.getAnnotations(pere.getIRI(), ontology,df.getRDFSLabel()).collect(Collectors.toSet()))
//							{
//							
//							String t = ((OWLLiteral)oa.getValue()).getLiteral();
//							if(!labelaxioms.contains(t)) {
//								//System.out.println(t);
//								iT.name = t;
//							}
//								
//							}
//						iT.top = top; // get the top of subOntology and add in InfoTerm class
//						
//						/*
//						 * The follow 4 lines is to add in a Link class the is a taxonomy
//						 * The method changetype is to transform the owl class to GO id
//						 */
//						iT.is_a.ancestors.addAll(changetype(reasoner.getSuperClasses(pere,false).entities().collect(Collectors.toSet()),ids));
//						iT.is_a.parents.addAll(changetype(reasoner.getSuperClasses(pere,true).entities().collect(Collectors.toSet()),ids));
//						iT.is_a.descendants.addAll(changetype(reasoner.getSubClasses(pere,false).entities().collect(Collectors.toSet()),ids));
//						iT.is_a.childrens.addAll(changetype(reasoner.getSubClasses(pere,true).entities().collect(Collectors.toSet()),ids));
//
//						if(iT.is_a.childrens.isEmpty()) {
//							leavesISA.add(iT.toString());
//						}
//
//						list2.addAll(reasoner.getSubClasses(pere,true).entities().collect(Collectors.toSet())); // Add in list2 every child in list2
//						/*
//						 * The owl sub class of axiom allow get the information about partOf and regulates. 
//						 */
//						
//						//System.out.println("END");
//						
//						for(OWLSubClassOfAxiom subclsaxiom : ontology.subClassAxiomsForSubClass(pere).collect(Collectors.toSet())){
//							String namereg = new String();
//							
//							for(OWLObjectProperty reg:subclsaxiom.getSuperClass().objectPropertiesInSignature().collect(Collectors.toSet())){
//								namereg = new String();
//								for(OWLAnnotation oa : EntitySearcher.getAnnotations(reg.getIRI(), ontology,ids).collect(Collectors.toSet()))
//									namereg = ((OWLLiteral)oa.getValue()).getLiteral();
//								
//								switch(namereg){
//								case "part of":
//								case "part_of":
//									for(OWLClass cpof : subclsaxiom.getSuperClass().classesInSignature().collect(Collectors.toSet())){
//										for(OWLAnnotation oa : EntitySearcher.getAnnotations(cpof.getIRI(), ontology,ids).collect(Collectors.toSet()))
//											{
//											iT.part_of.parents.add(((OWLLiteral)oa.getValue()).getLiteral());
//											iT.part_of.ancestors.add(((OWLLiteral)oa.getValue()).getLiteral());
//											}
//
//									}
//									break;
//								case "has_part":
//								case "has part":
//									for(OWLClass cpof : subclsaxiom.getSubClass().classesInSignature().collect(Collectors.toSet())){
//
//										for(OWLAnnotation oa : EntitySearcher.getAnnotations(cpof.getIRI(), ontology,ids).collect(Collectors.toSet()))
//											{
//											iT.part_of.childrens.add(((OWLLiteral)oa.getValue()).getLiteral());
//											iT.part_of.descendants.add(((OWLLiteral)oa.getValue()).getLiteral());
//											}
//											
//									}
//									break;
//								case "positively_regulates":
//								case "positively regulates":
//									for(OWLClass cpof : subclsaxiom.getSuperClass().classesInSignature().collect(Collectors.toSet())){
//										
//										for(OWLAnnotation oa : EntitySearcher.getAnnotations(cpof.getIRI(), ontology,ids).collect(Collectors.toSet()))
//											iT.positiveR =((OWLLiteral)oa.getValue()).getLiteral();
//											
//									}
//									break;
//								case "negatively_regulates":
//								case "negatively regulates":
//									for(OWLClass cpof : subclsaxiom.getSuperClass().classesInSignature().collect(Collectors.toSet())){
//										for(OWLAnnotation oa : EntitySearcher.getAnnotations(cpof.getIRI(), ontology,ids).collect(Collectors.toSet()))
//											iT.negativeR =((OWLLiteral)oa.getValue()).getLiteral();
//											
//									}
//									break;
//								case "regulates":
//									for(OWLClass cpof : subclsaxiom.getSuperClass().classesInSignature().collect(Collectors.toSet())){
//										for(OWLAnnotation oa : EntitySearcher.getAnnotations(cpof.getIRI(), ontology,ids).collect(Collectors.toSet()))
//											iT.regulate =((OWLLiteral)oa.getValue()).getLiteral();
//											
//									}
//									break;
//								}
//								
//								
//							}
//							
//							
//						}
//
//
//						info.put(id, iT);	// put all recovered information in the HashTable
//					}
//				}
//				list1.clear(); // clear all information in List1
//
//
//				if(!list2.isEmpty()){
//
//					list1.addAll(list2); // we add all information in list1
//					list2.clear(); // clear all information in list2
//					level = level + 1; // increase the depth
//				}
//				else
//					flag=0;
//			}
//
//		
//
//			this.allStringtoInfoTerm.putAll(info); // global HashTable where we add the local HashTable
//			this.subontology.get(top).setMaxDepth((double)level-1); // get the maximal Depth to subOntology
//			this.subontology.get(top).setnumbernodes(this.allStringtoInfoTerm.get(top).is_a.descendants.size());
//
//			int nisA = 0;
//			int npartOf = 0;
//
//			for(String t : this.allStringtoInfoTerm.get(top).is_a.descendants) {
//				//System.out.println(top + " "+nisA +" "+t +" "+ this.allStringtoInfoTerm.get(t));
//				
//				nisA = nisA + this.allStringtoInfoTerm.get(t).is_a.parents.size();
//				npartOf = npartOf + this.allStringtoInfoTerm.get(t).part_of.parents.size();
//
//			}
//			this.subontology.get(top).setnumberedge(nisA, npartOf);
//			this.subontology.get(top).addAllLeavesISA(leavesISA);
//
//
//		} 
//		
//		
//		
//		
//		/*
//		 * The followed step get the obsolete set to try the recover the information. That is possible because there are an annotation providing a term 
//		 * replacing the current obsolete term.
//		 */
//
//
////		for(OWLClass r : GOObsolete) { // For each obsolete owl class
////
////			boolean isDesreciated = true;
////			String idObs = r.toString().split("/")[r.toString().split("/").length-1].replace(">", "").replace("_", ":");
////			String id = new String();//
////			
////			boolean isReplaced = true;
////			while(isDesreciated == true) { // While boolean is true
////				if(!EntitySearcher.getAnnotations(r.getIRI(), ontology).toString().contains("IAO_0100001")) { // If there are no term that it replace the obsolete term
////					isReplaced = false;
////					List<String> consider = new ArrayList<String>(); 
////					consider.add("consider");
////					/*
////					 * We try search the condier annotation where several obsolete can be considered as other terms
////					 */
////					for(OWLAnnotation subclsaxiom : EntitySearcher.getAnnotations(r.getIRI(), ontology) ){
////						String sbcl= subclsaxiom.getProperty().toStringID();
////						String IAO = sbcl.split("/")[sbcl.split("/").length-1];
////						if(IAO.equals("oboInOwl#consider")) {
////							consider.add(((OWLLiteral)subclsaxiom.getValue()).getLiteral());
////						}
////					}
////					if(consider.size()>1) {// If we have any element
////						this.obsolete2consORrepl.put(idObs, consider);
////					}
////					isDesreciated = false;
////				}else {
////
////					for(OWLAnnotation subclsaxiom : EntitySearcher.getAnnotations(r.getIRI(), ontology) ){
////						String sbcl= subclsaxiom.getProperty().toStringID();
////						String IAO = sbcl.split("/")[sbcl.split("/").length-1];
////						if(IAO.equals("IAO_0100001")) { // If we find IAO_0100001 == replaceBy
////							IRI iri = IRI.create(subclsaxiom.getValue().toString());
////							
////							id = new String(); // get the GO ID
////							for(OWLAnnotation oa : EntitySearcher.getAnnotations(manager.getOWLDataFactory().getOWLClass(iri), ontology,ids))
////								id = ((OWLLiteral)oa.getValue()).getLiteral();
////							
////							if(id.contains("^^")) { // if not a GO id
////								OWLLiteral li = (OWLLiteral) subclsaxiom.getValue();
////								id = li.getLiteral();
////							}
////
////							r = id2owlclass.get(id);
////							if(r == null) { // if the literal don't provide a owl class
////								IRI i = IRI.create(owlprefix + id.replace(":", "_"));
////								r = manager.getOWLDataFactory().getOWLClass(i);
////							}
////							isDesreciated = false; 
////							for(OWLAnnotation oa : EntitySearcher.getAnnotations(r.getIRI(), ontology,deprecated))
////								isDesreciated = oa.isDeprecatedIRIAnnotation();
////							
////
////						}
////					}
////				}
////			}	
////
////			if(isReplaced==true) {
////				List<String> replaced = new ArrayList<String>();
////				replaced.add("replaced");
////				replaced.add(id);
////				this.obsolete2consORrepl.put(idObs,replaced);
////			}
////		}
//	}
//
//	/**
//	 * To change owl class set to GO id set
//	 */
//	public static List<String> changetype(Set<OWLClass> set, OWLAnnotationProperty id){
//		set.remove(manager.getOWLDataFactory().getOWLThing()); // remove owl:Thing
//		set.remove(manager.getOWLDataFactory().getOWLNothing()); // remove owl:Nothing
//		List<String> s = new ArrayList<String>();
//		for(OWLClass c : set){ // For each owl class
//			
//			for(OWLAnnotation oa : EntitySearcher.getAnnotations(c.getIRI(), ontology,id).collect(Collectors.toSet()))
//				s.add(((OWLLiteral)oa.getValue()).getLiteral());
//			
//		}
//		return s;
//
//
//	}
//
//	/**
//	 * The informationOnt method create the GlobalOntology object to the GSAn program. 
//	 * @param args
//	 * @return GlobalOntology
//	 */
//	public static GlobalOntology_OWLAPI5 informationOnt(String[] args) {
//
//		try {
//
//			IRI classIRI =  OWLRDFVocabulary.OWL_THING.getIRI(); // Get owl:Thing class
//
//
//			/*
//			 *  We first need to obtain a copy of an OWLOntologyManager, which, as the name
//			 *  suggests, manages a set of ontologies. 
//			 */
//			manager = OWLManager.createOWLOntologyManager();
//
//
//			File file = new File(args[0]);
//			String source = args[1];
//			ontology = manager.loadOntologyFromOntologyDocument(file.getAbsoluteFile()); //Charging Ontology
//
//
//			System.out.println("[GlobalOntology] Charging reasoner...");
//			OWLReasonerFactory reasonerFactoryin = new ElkReasonerFactory(); // Charging ELK reasoner and hierarchy
//
//			GlobalOntology_OWLAPI5 simpleHierarchy = new GlobalOntology_OWLAPI5(manager, reasonerFactoryin,source); // Create a new GlobalOntology object with the given reasoner.
//
//
//			OWLClass clazz = manager.getOWLDataFactory().getOWLClass(classIRI);// Get owl class of owl:Thing
//			/*
//			 * Completing information to GlobalOntology object
//			 */
//			simpleHierarchy.printHierarchy(ontology, clazz, source ); // Create the Object of Term information
//			simpleHierarchy.completingThePath(); // Get PartOf terms (method created for me)
//			simpleHierarchy.Leaves(); // Get the leaves terms in is a taxonomy
//			//			simpleHierarchy.Both(); // create
//			
//			simpleHierarchy.GetICs(); // Add the Intrinsic IC for every GO term
//			simpleHierarchy.goUniverseIC(); // Add the GO Universal IC Mazandu et al 2013.
//			
//			simpleHierarchy.GetDistanceTerms(); // Recover all distance between terms in the taxonomy
//			
//			for(String o : simpleHierarchy.subontology.keySet()) {
//				List<Double> dist = new ArrayList<Double>();
//				for(String t : simpleHierarchy.subontology.get(o).getLeavesISA()) {
//					InfoTerm it = simpleHierarchy.allStringtoInfoTerm.get(t);
//					dist.add(it.distancias.get(o));
//				}
//				simpleHierarchy.subontology.get(o).setMaxDistance(Collections.max(dist));
//			}
//			
//			
//			Map<String, DescriptiveStatistics> mapSeco = new HashMap<>();
//			Map<String, DescriptiveStatistics> mapZhou = new HashMap<>();
//			Map<String, DescriptiveStatistics> mapSanchez = new HashMap<>();
//			Map<String, DescriptiveStatistics> mapMaz = new HashMap<>();
//			
//				mapSeco.put(source, new DescriptiveStatistics());
//				mapZhou.put(source, new DescriptiveStatistics());
//				mapSanchez.put(source, new DescriptiveStatistics());
//				mapMaz.put(source, new DescriptiveStatistics());
//			
//			
//			for(String t : simpleHierarchy.allStringtoInfoTerm.keySet()){
//				
//				mapSeco.get(source).addValue(simpleHierarchy.allStringtoInfoTerm.get(t).ICs.get(0));
//				mapZhou.get(source).addValue(simpleHierarchy.allStringtoInfoTerm.get(t).ICs.get(1));
//				mapSanchez.get(source).addValue(simpleHierarchy.allStringtoInfoTerm.get(t).ICs.get(2));
//				mapMaz.get(source).addValue(simpleHierarchy.allStringtoInfoTerm.get(t).ICs.get(3));
//			}
//			simpleHierarchy.IC2DS.put("seco", mapSeco);
//			simpleHierarchy.IC2DS.put("zhou", mapZhou);
//			simpleHierarchy.IC2DS.put("sanchez", mapSanchez);
//			simpleHierarchy.IC2DS.put("mazandu", mapMaz);
//			
//			
//			
//			return simpleHierarchy;
//
//		} catch (Exception e) {
//			System.out.println("There are a big problem to create the GlobalOntology object");
//			e.printStackTrace();
//			System.exit(0);
//			return null;
//		}
//
//
//	}
//	
////	public void prokaryoteOnto() {
////		
////		try {
////		Map<String,InfoTerm> info = new HashMap<>();
////		
////		for(String sub : this.subontology.keySet()) {
////		List<String> termsLevel = new LinkedList<>();
////		List<String> listPivot = new LinkedList<>();
////		Set<String> leavesIsA = new HashSet<>();
////	
////		boolean bol = false;
////		termsLevel.add(sub);
////		int level = 0;
////		while(!bol) {
////			for(String k : termsLevel) {
////			
////			InfoTerm it = this.allStringtoInfoTerm.get(k);
////			if(k.equals("GO:0008150"))
////				//System.out.println(this.termsProks.size());
////			if(it.inProk) {
////				InfoTerm it_prok = new InfoTerm(k,level,true);
////				it_prok.name=it.toName();
////				Queue<String> papa = new LinkedList<>(it.is_a.parents);
////				Set<String> solPapa = new HashSet<>();
////				while(!papa.isEmpty()) {
////					String p = papa.poll();
////						if(this.allStringtoInfoTerm.get(p).inProk) {
////							solPapa.add(p);
////						}else {
////							papa.addAll(this.allStringtoInfoTerm.get(p).is_a.parents);
////						}
////					
////					
////				}
////				Queue<String> childs = new LinkedList<>(it.is_a.childrens);
////				Set<String> solChilds = new HashSet<>();
////				while(!childs.isEmpty()) {
////					String p = childs.poll();
////						if(this.allStringtoInfoTerm.get(p).inProk) {
////							solChilds.add(p);
////						}else {
////							childs.addAll(this.allStringtoInfoTerm.get(p).is_a.childrens);
////						}
////					
////					
////				}
////				
////				Set<String> ancestors = new HashSet<>(it.is_a.ancestors);
////				ancestors.retainAll(this.termsProks);
////				Set<String> descendants = new HashSet<>(it.is_a.descendants);
////				descendants.retainAll(this.termsProks);
//////				Set<String> leaves = new HashSet<>(it.is_a.descLeaves);
//////				leaves.retainAll(this.termsProks);
////				
////				//System.out.println(this.allStringtoInfoTerm.get(k).toName());
////				
////				it_prok.is_a.parents.addAll(solPapa);
////				it_prok.is_a.childrens.addAll(solChilds);
////				it_prok.is_a.ancestors.addAll(ancestors);
////				it_prok.is_a.descendants.addAll(descendants);
////				
////				if(solChilds.isEmpty()) {
////					leavesIsA.add(it_prok.id);
////				}
//////				it_prok.is_a.descLeaves.addAll(leaves);
////				
////				
////				/*
////				 * Take firs the part of and after the is a is not a error.
////				 * I choose do that because the part of of part of are considered
////				 * for me as ancestors. 
////				 */
////				
////				Queue<String> papapf = new LinkedList<>(it.part_of.parents);
////				Set<String> solPapapf = new HashSet<>();
////				while(!papapf.isEmpty()) {
////					String p = papapf.poll();
////						if(this.allStringtoInfoTerm.get(p).inProk) {
////							solPapapf.add(p);
////						}else {
////							papapf.addAll(this.allStringtoInfoTerm.get(p).is_a.parents);
////						}
////					
////					
////				}
////				Queue<String> childspf = new LinkedList<>(it.part_of.childrens);
////				Set<String> solChildspf = new HashSet<>();
////				while(!childspf.isEmpty()) {
////					String p = childspf.poll();
////						if(this.allStringtoInfoTerm.get(p).inProk) {
////							solChildspf.add(p);
////						}else {
////							childspf.addAll(this.allStringtoInfoTerm.get(p).is_a.childrens);
////						}
////					
////					
////				}
////				
//////				Set<String> ancestorspf = new HashSet<>(it.part_of.ancestors);
//////				ancestorspf.retainAll(this.termsProks);
//////				Set<String> descendantspf = new HashSet<>(it.part_of.descendants);
//////				descendantspf.retainAll(this.termsProks);
//////				Set<String> leavespf = new HashSet<>(it.part_of.descLeaves);
//////				leavespf.retainAll(this.termsProks);
////				
////				it_prok.part_of.parents.addAll(solPapapf);
////				it_prok.part_of.childrens.addAll(solChildspf);
////				it_prok.part_of.ancestors.addAll(solPapapf);
////				it_prok.part_of.descendants.addAll(solChildspf);
//////				it_prok.part_of.descLeaves.addAll(leavespf);
////				
////				
////				if(this.termsProks.contains(it.positiveR)) {
////					it_prok.positiveR = it.positiveR;
////				}
////				if(this.termsProks.contains(it.negativeR)) {
////					it_prok.negativeR = it.negativeR;
////				}
////				if(!this.termsProks.contains(it.regulate)) {
////					it_prok.regulate = it.regulate;
////				}
////				
////				it_prok.top = it.top;
////				
////				info.put(it.id, it_prok);
////				listPivot.addAll(it_prok.is_a.childrens);
////			}
////			
////			}
////			
////			termsLevel.clear();
////			if(!listPivot.isEmpty()) {
////				termsLevel.addAll(listPivot);
////				listPivot.clear();
////				level++;
////			}else {
////				bol=true;
////			}
////			
////		}
////		
////		
////		
////		this.subontology.get(sub).setMaxDepth((double)level-1); // get the maximal Depth to subOntology
////		//System.out.println(info.get("GO:0008150").is_a.descendants);
////		this.subontology.get(sub).setnumbernodes(info.get(sub).is_a.descendants.size());
////
////		int nisA = 0;
////		int npartOf = 0;
////
////		for(String t : info.get(sub).is_a.descendants) {
////			nisA = nisA + info.get(t).is_a.parents.size();
////			npartOf = npartOf + info.get(t).part_of.parents.size();
////
////		}
////		
////		
////		
////		
////		this.subontology.get(sub).setnumberedge(nisA, npartOf);
////		this.subontology.get(sub).ClearLeavesISA();
////		this.subontology.get(sub).ClearMaxIC();
////		this.subontology.get(sub).addAllLeavesISA(leavesIsA);
////		}
////		
////		this.allStringtoInfoTerm.clear();
////		this.allStringtoInfoTerm.putAll(info);
////		this.Leaves(); // Get the leaves terms in is a taxonomy
////		this.completingThePath();
////		this.GetDistanceTerms(); // Recover all distance between terms in the taxonomy
////		this.GetICs(); // Add the Intrinsic IC for every GO term
////		this.goUniverseIC(); // Add the GO Universal IC Mazandu et al 2013.
////		
////		
////		Map<String, DescriptiveStatistics> mapSeco = new HashMap<>();
////		Map<String, DescriptiveStatistics> mapZhou = new HashMap<>();
////		Map<String, DescriptiveStatistics> mapSanchez = new HashMap<>();
////		Map<String, DescriptiveStatistics> mapMaz = new HashMap<>();
////		for(String ont : this.subontology.keySet()) {
////			mapSeco.put(ont, new DescriptiveStatistics());
////			mapZhou.put(ont, new DescriptiveStatistics());
////			mapSanchez.put(ont, new DescriptiveStatistics());
////			mapMaz.put(ont, new DescriptiveStatistics());
////		}
////		
////		for(String t : this.allStringtoInfoTerm.keySet()){
////			
////			mapSeco.get(this.allStringtoInfoTerm.get(t).top).addValue(this.allStringtoInfoTerm.get(t).ICs.get(0));
////			mapZhou.get(this.allStringtoInfoTerm.get(t).top).addValue(this.allStringtoInfoTerm.get(t).ICs.get(1));
////			mapSanchez.get(this.allStringtoInfoTerm.get(t).top).addValue(this.allStringtoInfoTerm.get(t).ICs.get(2));
////			mapMaz.get(this.allStringtoInfoTerm.get(t).top).addValue(this.allStringtoInfoTerm.get(t).ICs.get(3));
////		}
////		this.IC2DS.put("seco", mapSeco);
////		this.IC2DS.put("zhou", mapZhou);
////		this.IC2DS.put("sanchez", mapSanchez);
////		this.IC2DS.put("mazandu", mapMaz);
////		}catch (Exception e) {
////			e.printStackTrace();
////			// TODO: handle exception
////		}
////		
////	}
//	
//	public double getPercentile(int per, String author,String ont) {
//		//System.out.println(this.IC2DS.get(author));
//		return this.IC2DS.get(author).get(ont).getPercentile(per);
//	}
//
//	/**
//	 * This method recover the IC created by Nuno Seco, Zhou and David Sanchez
//	 */
//
//	public void GetICs() {
//		for(Entry<String, OntoInfo> topInfo : this.subontology.entrySet()) {
//			String topOnt = topInfo.getKey();
//			Set<Double> nunoSet = new HashSet<Double>(); // Seco IC values set
//			Set<Double> zhouSet = new HashSet<Double>(); // Zhou IC values set
//			Set<Double> sanchezSet = new HashSet<Double>(); // Sanchez IC values set
//			InfoTerm topTerm = this.allStringtoInfoTerm.get(topOnt);
//			ArrayList<Double> lic = new ArrayList<Double>();
//			ArrayList<Double> lpob = new ArrayList<Double>();
////			System.out.println(topTerm);
////			System.out.println(topTerm.is_a.descendants);
//			/*
//			 * The first values is to TopTerm in subOntology
//			 */
//			lic.add(0.);
//			lic.add(0.);
//			lic.add(0.);
//			lpob.add(1.);
//			lpob.add(1.);
//			lpob.add(1.);
//			topTerm.addICs(lic);
//
//			for(String ter : topTerm.is_a.descendants){ // For each term descedant of Top of subOntology
////				System.out.println(ter);
//				InfoTerm t = this.allStringtoInfoTerm.get(ter); // Get the InfoTerm object
////				System.out.println(t);
//				/*
//				 * Compute the Seco and Sanchez probability 
//				 */
//				double probd = ((double) t.is_a.descendants.size()+1.)/ ((double) topTerm.is_a.descendants.size()+1.);
//				double probsanchez = (double) ((double)t.is_a.descLeaves.size()/(double)t.is_a.ancestors.size() + 1.)/(double)topTerm.is_a.descLeaves.size(); 
//				/*
//				 * Compute the Seco, Zhou and Sanchez IC. Zhou IC is a improvement of Seco IC applying the Depth. 
//				 */
//				double icnuno = 1.-Math.log((double) t.is_a.descendants.size()+1.)/Math.log((double) topTerm.is_a.descendants.size()+1.);
//				double iczhou = 0.5*(icnuno)+(1.-0.5)*(Math.log(t.depth())/Math.log(topInfo.getValue().maxDepth()));
//				double icsanchez = (double) - Math.log(probsanchez);///(double)-Math.log(1./(double)topTerm.is_a.descLeaves.size());
//				/*
//				 * Add the IC results in list to infoTerm
//				 */
//				lic = new ArrayList<Double>();
//				lic.add(icnuno);	
//				lic.add(iczhou);
//				lic.add(icsanchez);
//				/*
//				 * Add the IC results in set of all values
//				 */
//				nunoSet.add(icnuno);
//				zhouSet.add(iczhou);
//				sanchezSet.add(icsanchez);
//				/*
//				 * Add the probability results in list to infoTerm
//				 */
//				lpob = new ArrayList<Double>();
//				lpob.add(probd);
//				lpob.add(probd);
//				lpob.add(probsanchez);
//
//				t.addICs(lic); // ADD IC and probability list in InfoTerm object
//
//
//
//
//
//
//			}
//			
//			topInfo.getValue().addMaxIC(Collections.max(nunoSet));
//			topInfo.getValue().addMaxIC(Collections.max(zhouSet));
//			topInfo.getValue().addMaxIC(Collections.max(sanchezSet));
//		}
//	}
//	/**
//	 * That is a idea to create a new hybrid information content using the Seco IC but using the specific terms of a organism.	
//	 * @param sub
//	 * @param goa
//	 */
//	public void hybridIC(String sub, Annotation goa) {
//
//		Set<String> terms = new HashSet<String>(); // Terms set of annotated a complete genome
//		Map<String,Set<String>> go2descendantORG = new HashMap<>(); // Map if GO terms a the descedant most specific to a specific organism
//		for(String gen : goa.annotation.keySet()) { // For each annotated gene
//			for(String t : goa.annotation.get(gen).getTerms(sub)) { // For each term annotated to a given gene
//				terms.add(t); // Add in terms set
//				terms.addAll(this.allStringtoInfoTerm.get(t).is_a.ancestors); // Add all ancestors in terms set
//			}
//		}
//		Set<String> desc = new HashSet<String>(this.allStringtoInfoTerm.get(sub).is_a.descendants); // Get its descendants
//		desc.retainAll(terms);
//		go2descendantORG.put(sub,desc); 
//		this.allStringtoInfoTerm.get(sub).ICs.add(0.);
//		Set<Double> icsTest = new HashSet<>();
//		for(String t : terms) { // For each term in terms set
//			desc = new HashSet<String>(this.allStringtoInfoTerm.get(t).is_a.descendants); // Get its descendants
//			desc.retainAll(terms); // Intersection with terms set
//			go2descendantORG.put(t,desc); // Push the information in the Map
//		//	double probd = ((double) go2descendantORG.get(t).size()+1.)/ ((double) go2descendantORG.get(sub).size()+1.); // compute probability
//			double icnuno = 1.-Math.log((double) go2descendantORG.get(t).size()+1.)/Math.log((double)go2descendantORG.get(sub).size()+1.); //compute IC
//
//			this.allStringtoInfoTerm.get(t).ICs.add(icnuno);
//			icsTest.add(icnuno);
//		}
//		this.subontology.get(sub).addMaxIC(Collections.max(icsTest)); // Get Max IC and add to Map top2MaxIC
//		//go2descendantORG.putAll(go2descendantORG);
//
//	}
//	/**
//	 * After get the GOA annotation, we compute the extrinsic IC. Also, we use a new hybrid IC using Annotation IC instad Seco IC to Zhou IC.
//	 */
//	/*
//	 * The procedure is similar to GetICs method
//	 */
//	public void addAnnotationICs(){
//		Set<Double> annotationSet = new HashSet<Double>(); // Annotation IC set
//		Set<Double> annotationZhouSet = new HashSet<Double>(); // Annotation improve apply Zhou IC set
//
//		//		Set<String> test = new HashSet<String>();
//		//		for(String topOnt : this.subontology) { 
//		//			test.addAll(this.allStringtoInfoTerm.get(topOnt).genome);
//		//		}
//		for(Entry<String, OntoInfo> topInfo : this.subontology.entrySet()) {
//			String topOnt = topInfo.getKey(); // For each subOntology
//			InfoTerm topTerm = this.allStringtoInfoTerm.get(topOnt);
//			topTerm.ICs.add(0.);
//			topTerm.ICs.add(0.);
//
//			for(String ter : topTerm.is_a.descendants){ // For each terms descendant to top
//				
//				InfoTerm t = this.allStringtoInfoTerm.get(ter);
//
//				if(t.genome.size()>0) {
//					
//				double probg = ((double)t.genome.size())/((double)topTerm.genome.size());
//				double icg = - Math.log(probg);///-Math.log10((1./((double)topTerm.genome.size()))) ;
//				t.ICs.add(icg);
//
//				double iczhou = 0.5*(icg/-Math.log(1./(double)topTerm.genome.size()))+(1.-0.5)*(Math.log(this.allStringtoInfoTerm.get(ter).depth())/Math.log(topInfo.getValue().maxDepth()));	
//				t.ICs.add(iczhou);
//
//				if(probg>0.) {
//					annotationSet.add(icg);
//					annotationZhouSet.add(iczhou);
//					//					annotationSetNN.add(icgnn);
//				}
//			}
//
//
//			}
////			System.out.println(annotationSet);
////			System.out.println(topInfo.getValue().maxIC);
//			topInfo.getValue().addMaxIC(Collections.max(annotationSet));
//			topInfo.getValue().addMaxIC(Collections.max(annotationZhouSet));
//
//
//
//		}
//
//
//	}
//	public void getLocalInformation() {
//
//
//		for(String t : this.allStringtoInfoTerm.keySet()) {
//			InfoTerm it = this.allStringtoInfoTerm.get(t);
//			it.sIC = -Math.log((double)it.geneSet.size()/(double) this.allStringtoInfoTerm.get(it.top).geneSet.size());
//			it.ivalue = it.ICs.get(0)/it.sIC;
//
//		}
//
//
//
//	}
//
//
//
//
//	/**
//	 * Method to get the distance for every terms making easy recover the information after in others process.
//	 */
//	public void GetDistanceTerms(){
//		/*
//		 * Get distances between terms
//		 */
//		Set<Edges> edges = new HashSet<Edges>();
//		for(String m : this.allStringtoInfoTerm.keySet()){
//			Edges[] lien;
//			ArrayList<Edges> e = new ArrayList<Edges>();
//			InfoTerm it = this.allStringtoInfoTerm.get(m);
//			double icC = it.ICs.get(1);
//			for(String pere : it.is_a.parents){
//				InfoTerm itP = this.allStringtoInfoTerm.get(pere);
//				double icP = itP.ICs.get(1);
//				
//				Edges ed = new Edges(it,this.allStringtoInfoTerm.get(pere),Math.abs(icP-icC));
////				Edges ed = new Edges(it,this.allStringtoInfoTerm.get(pere),1);
//				
//				e.add(ed);
//				edges.add(ed);
//			}
////			for(String pere : it.part_of.ancestors){
////				Edges ed = new Edges(this.allStringtoInfoTerm.get(m),this.allStringtoInfoTerm.get(pere),1.0);
////				e.add(ed);
////				edges.add(ed);
////			}
//			// To save distances for each node
//			lien =  new Edges[e.size()];
//			for(int n = 0; n<e.size();n++){
//				lien[n] = e.get(n);
//			}
//			this.allStringtoInfoTerm.get(m).adjacencies = lien;
//		}
//		//this.LienMax = edges.size();
//		// Saving distance for each terms
//		this.SaveDistance();
//	}
//	
//	/**
//	 * Method depends of GetDistanceTerms. That method compute A* algorithm to get the distance between two terms
//	 */
//	private void SaveDistance(){
//
//		for(String ter: this.allStringtoInfoTerm.keySet()){
//			InfoTerm t = this.allStringtoInfoTerm.get(ter);
//			Hashtable<String,Double> distancias = new Hashtable<String,Double>();
//			for(String p : t.is_a.ancestors){
//				AstarSearchAlgo.AstarSearch(t,this.allStringtoInfoTerm.get(p));
//				double valor = this.allStringtoInfoTerm.get(p).g_scores;// keep disance
//				distancias.put(p, valor);
//			}
//			//			for(String p : t.part_of.ancestors){
//			//				AstarSearchAlgo.AstarSearch(t,this.allStringtoInfoTerm.get(p));
//			//				double valor = this.allStringtoInfoTerm.get(p).g_scores;// keep disance
//			//				distancias.put(p, valor);
//			//			}
//			distancias.put(ter, 0.);
//			t.SaveDistance(distancias);
//
//		}
//
//	}
//	public void AggregateIC() {
//
//		for(String t : this.allStringtoInfoTerm.keySet()) {
//			
//			InfoTerm it = this.allStringtoInfoTerm.get(t);
//			if(it.genome.size()>0) {
//			if(!it.id.equals(it.top)) {
//				//System.out.println(it.ICs.size());
//				it.sValueIC = 1./(1. + Math.exp(-1./it.ICs.get(4)));
//			}else {
//				it.sValueIC = 1;
//
//			}}
//
//
//
//
//
//
//		}
//
//		for(String t : this.allStringtoInfoTerm.keySet()) {
//			InfoTerm it = this.allStringtoInfoTerm.get(t);
//			if(it.genome.size()>0) {
//			double aic = it.sValueIC;
//			for(String anc : it.is_a.ancestors) {
//				aic = aic + this.allStringtoInfoTerm.get(anc).sValueIC;
//			}
//			it.aggregateIC = aic;
//		}
//		}
//
//		//		System.exit(0);
//
//	}
//
//	public void goUniverseIC() {
//		
//
//		for(Entry<String,OntoInfo> sub : this.subontology.entrySet()) {
//			OntoInfo oi = sub.getValue();
//			this.allStringtoInfoTerm.get(sub.getKey()).ICs.add(0.);
//			this.allStringtoInfoTerm.get(sub.getKey()).alphaBetaMazandu[0] = 1.;
//			this.allStringtoInfoTerm.get(sub.getKey()).alphaBetaMazandu[1] = 0.;
//			Set<Double> setMax = new HashSet<>();
//		//	Set<Double> probMax = new HashSet<>();
//			for(String lt : oi.getLeavesISA()) {
//				
//				double[] dd = this.goUniverseIC(this.allStringtoInfoTerm.get(lt));
//				setMax.add(-Math.log(dd[0])-dd[1]*Math.log(10));
//				//System.out.println(-Math.log(Double.parseDouble(dd[0]))-Double.parseDouble(dd[1])*Math.log(10));
//				
//				//			if(dd == 0) {
//				//				System.out.println(lt + " " + dd);
//				//				System.exit(0);
//				//			}
//				//			probMax.add(dd);
//			}
//
//			Double max = Collections.max(setMax);
//
//			oi.addMaxIC(max);
//
//			//System.out.println("GO Universel " + sub.getKey() + " value = " + max );
//		}
//
//
//
//	}
//	public double[] goUniverseIC(InfoTerm it) {
//
//		double alpha = 1.;
//		double beta = 0.;
//		for(String par : it.is_a.parents) {
//
//			
//			if(!par.equals(it.top)) {
//				double[] pereAlphaBeta = goUniverseIC(this.allStringtoInfoTerm.get(par));
//				double division = pereAlphaBeta[0]/(double) this.allStringtoInfoTerm.get(par).is_a.childrens.size();
//				double b = Math.floor(Math.log10(division));
//				double a = division/Math.pow(10,b); 
//				
//				alpha = alpha* a;
//				beta = beta + pereAlphaBeta[1] + b;
//				
//				
//				}
//			else {
//				double division = 1./(double) this.allStringtoInfoTerm.get(par).is_a.childrens.size();
//				double b = Math.floor(Math.log10(division));
//				double a = division/Math.pow(10,b); 
//				alpha = alpha* a;
//				beta = beta + b;
//			}
//		}
//		it.alphaBetaMazandu[0] = alpha;
//		it.alphaBetaMazandu[1] = beta;
//	
//		if(it.ICs.size()==3) {
//			it.ICs.add(-Math.log(alpha)-beta*Math.log(10));
//		}
//		//System.out.println(it.ICs.size());
//		//System.out.println("\t"+it.id + " " +probX);
//		return it.alphaBetaMazandu;
//	}
////	public String[] goUniverseIC(InfoTerm it, NumberFormat nf) {
////
////		Double alpha = 1.;
////		Double beta = 0.;
////		for(String par : it.is_a.parents) {
////
////			
////			if(!par.equals(it.top)) {
////				String[] pereAlphaBeta = goUniverseIC(this.allStringtoInfoTerm.get(par), nf);
////				double division = Double.parseDouble(pereAlphaBeta[0])/(double) this.allStringtoInfoTerm.get(par).is_a.childrens.size();
////				String[] alphaBeta = nf.format(division).toString().split("E");
////				alpha = alpha* Double.parseDouble(alphaBeta[0]);
////				beta = beta + Double.parseDouble(pereAlphaBeta[1]) + Double.parseDouble(alphaBeta[1]);
////				
////				
////				}
////			else {
////				double division = 1./(double) this.allStringtoInfoTerm.get(par).is_a.childrens.size();
////				String[] alphaBeta = nf.format(division).toString().split("E");
////				alpha = alpha* Double.parseDouble(alphaBeta[0]);
////				beta = beta + Double.parseDouble(alphaBeta[1]);
////			}
////		}
////		it.alphaBetaMazandu[0] = alpha.toString();
////		it.alphaBetaMazandu[1] = beta.toString();
////	
////		if(it.ICs.size()==3) {
////			it.ICs.add(-Math.log(Double.parseDouble(alpha.toString()))-Double.parseDouble(beta.toString())*Math.log(10));
////		}
////		//System.out.println(it.ICs.size());
////		//System.out.println("\t"+it.id + " " +probX);
////		return it.alphaBetaMazandu;
////	}
//
////	public BigDecimal goUniverseICTest(InfoTerm it) {
////
////		BigDecimal probX = new BigDecimal(1);
////		if(it.probMazandu==0) {
////			
////		
////		
////		for(String par : it.is_a.parents) {
////
////			
////			if(!par.equals(it.top)) {
////				probX = probX.multiply(this.goUniverseIC(this.allStringtoInfoTerm.get(par)).divide(new BigDecimal(this.allStringtoInfoTerm.get(par).is_a.childrens.size()), 10, RoundingMode.UP));
////				}
////			else {
////				//System.out.println(it.top+":::" +it.top+ " : "+ this.allStringtoInfoTerm.get(par).is_a.childrens.size());
////				probX = probX.multiply(new BigDecimal(1).divide(new BigDecimal(this.allStringtoInfoTerm.get(par).is_a.childrens.size()),10, RoundingMode.UP));
////			}
////		}
////	
////		//System.out.println(it.top+":::" +it+ " : "+ it.is_a.childrens.size()+ "  :  " +probX);
////		//System.out.println(probX.doubleValue());
////		it.ICs.add(-BigDecimalMath.log(probX,new MathContext(20)).doubleValue());
////		it.probMazandu = probX.doubleValue();
////	}else {
////		probX = new BigDecimal(it.probMazandu);
////	}
////	
////		System.out.println(it.ICs.size());
////		//System.out.println("\t"+it.id + " " +probX);
////		return (probX);
////	}
//
//
//}
//
//
//
//
