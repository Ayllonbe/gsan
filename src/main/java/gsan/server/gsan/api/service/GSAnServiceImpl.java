package gsan.server.gsan.api.service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import gsan.distribution.algorihms.sumSetProblem.SetCoverAaron;
import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.annotation.ChooseAnnotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import gsan.distribution.gsan_api.ontology.TransformDagToTree;
import gsan.distribution.gsan_api.read_write.ReadFile;
import gsan.distribution.gsan_api.read_write.writeSimilarityMatrix;
import gsan.distribution.gsan_api.run.representative.AlgorithmRepresentative;
import gsan.distribution.gsan_api.run.representative.Cluster;
import gsan.server.gsan.api.service.jpa.taskRepository;
import gsan.server.gsan.api.service.model.task;
import gsan.server.singleton.graphSingleton;

@Service
public class GSAnServiceImpl implements GSAnService {

	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	@Async("workExecutor")
	public void runService(taskRepository tR, task t,List<String> query, String organism, boolean IEA,List<String> ontology,
			String ssMethod,int geneSupport,  int percentile,int ids) {
		Map<String,Object> process = new HashMap<>();
		
			log.debug("Beging process n° " + t.getId());
			String goa_file = ChooseAnnotation.annotation(organism);
			GlobalOntology go = graphSingleton.getGraph();
			log.debug("Charging Annotation file");
			Annotation GOA ;
		
				try {
					List<List<String>> 	goaTable = getFile(goa_file);
				GOA = new Annotation(goaTable, go, IEA,ids);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					t.setError(true);
					t.setMSGError(6);
					GOA=null;
				}
					
	
			
			process.putAll(gsanService(query,GOA,go, ontology, ssMethod,
					geneSupport,  percentile));
			process.put("organism", organism);
			finishing(tR,t, process);		
		
	}

	@Override
	public Map<String,Object> runService(List<String> query, String organism, boolean IEA,List<String> ontology,
			String ssMethod,int geneSupport, int percentile,int ids) {
		try {
			String goa_file = ChooseAnnotation.annotation(organism);
			GlobalOntology go = graphSingleton.getGraph();
			Annotation GOA ;
		
				try {
					List<List<String>> 	goaTable = getFile(goa_file);
				GOA = new Annotation(goaTable, go,IEA,ids);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					
					e1.printStackTrace();
					GOA=null;
				}
						
			Map<String,Object> process = gsanService(query,GOA,go, ontology, ssMethod,geneSupport,  percentile);
			
			return process;
		}
		catch(Exception ie) {
			log.error(""+ie);
			ie.printStackTrace();
			return null;
		}
	}
	
	@Override
	@Async("workExecutor")
	public void runService(taskRepository tR, task t,List<String> query, boolean IEA,List<String> ontology,
			String ssMethod, int geneSupport,  int percentile, String goa_file,int ids) {
			Map<String,Object> process = new HashMap<>();
			
			log.debug("Beging process n° " + t.getId());
			GlobalOntology go = graphSingleton.getGraph();
			log.debug("Charging Annotation file");
			Annotation GOA ;
		
				try {
					List<List<String>> 	goaTable = getGAFFile(goa_file);
					
					//System.out.println(goaTable.get(0));
				GOA = new Annotation(goaTable, go, true,ids);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					t.setError(true);
					t.setMSGError(6);
					e1.printStackTrace();
					GOA=null;
				}
					
	
			
			process.putAll(gsanService(query,GOA,go , ontology, ssMethod, geneSupport,  percentile));
		    process.put("organism", "");
			finishing(tR,t, process);	
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private void finishing(taskRepository tR,task t, Map<String,Object> process) {
		try {
			JSONObject jo = new JSONObject();
			jo.putAll(process);
			PrintWriter pw = new PrintWriter("src/main/tmp/results/"+t.getId()+".json");
			pw.print(jo.toJSONString());
			pw.close();
			boolean finish = (boolean)  process.get("boolean");
			if(finish && (int) process.get("msg") == 0)
				t.setfinish(true);
			else
				t.setError(true);
				t.setMSGError((int) process.get("msg"));
			
			tR.save(t);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				t.setError(true);
				t.setMSGError(2);
				e.printStackTrace();
			}
			
			
			
			log.debug("Ending process n° " + t.getId());

	}
	
	private List<List<String>> getFile(String o) throws IOException{
		//File reacf = new File ("src/main/resources/static/integration/reac_human.gaf");
		List<List<String>> goaTable = ReadFile.ReadAnnotation("src/main/resources/static/AssociationTAB/"+o);
		 
		//goaTable.addAll(ReadFile.ReadAnnotation(reacf.getAbsolutePath()));
		return goaTable;
	}
	private List<List<String>> getGAFFile(String o) throws IOException{
		
		
		List<List<String>> gafList = new ArrayList<>();
		try {
			
			String[] arr = o.split("\n");
		
			for(String a : arr) {
				List<String> line = new ArrayList<String>();
				String[] col = a.split("\t");
				if(!col[0].contains("#")&&!col[0].contains("!") ) {
				
				for(String c : col) {
					line.add(c);
				}
				gafList.add(line);
			}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return gafList;
	}
	
	
	private Map<String, Object> gsanService(List<String> query, Annotation GOA, GlobalOntology go,List<String> ontology,
			String ssMethod, 
			int geneSupport, int percentile) {
		int msg_code = 0;
		try {
		int ic_inc = 3;
		
		List<String> genesList = new ArrayList<>(new HashSet<String>(query));

		//List<List<String>> goaTable;
		String author = ICauthor(ic_inc);
		//	goaTable = ReadFile.ReadAnnotation(goaf.getAbsolutePath());
		//System.out.println("src/main/resources/static/AssociationTAB/"+goa_file);
	

			
//		System.out.println("end test");
		Map<String,Double> Mappercentile = new HashMap<>();
		log.debug("Charging annotated gene in GO terms");
			go.AddTermToGenome(GOA);	
		try {
			log.debug("Computing ICs...");
			go.addAnnotationICs();
			go.AggregateIC();}
		catch(Exception e) {
			log.error("Imposible to charge the IC. Normally the annotation file is broken.");
			msg_code = 6;
		}
			
			log.debug("Computing Percentiles...");	
			for(String subont : go.sourceSet) {
//				System.out.println(subont);
				Mappercentile.put(subont, go.getPercentile(percentile, author, subont));
//				System.out.println("Percentile : "+go.getPercentile(percentile, author, subont));
			} 
			
			log.debug("Reducing annotation...");
			Annotation  GOAred = Annotation.redondancyReduction(GOA,go);
			Annotation GOAincom = Annotation.icIncompleteReduction(GOAred,go,ic_inc, Mappercentile);

		Set<String> termsInc = new HashSet<String>();
		//System.out.println(ontology);
		log.debug("Recovering terms to analyse the gene set");
		for(String ont : ontology) {;
			List<String> termsonto = GOAincom.getTerms(genesList,ont,go);
			if(termsonto!=null)
				termsInc.addAll(termsonto);
		}
		if(termsInc.isEmpty()){
			msg_code = msg_code>0?msg_code: 3;
			throw new java.lang.NullPointerException("line 255 - termsInc is empty");
			}


		Map<String,Object> map = new HashMap<>();
		
		map.putAll(GSAnMethod(genesList, ontology,go,GOAincom, ssMethod, geneSupport,Mappercentile,termsInc));
		
		if(msg_code>0) map.put("msg",msg_code);
        return map;
		
		}catch(Exception e) {
			log.error("" + e);
			Map<String,Object> map = new HashMap<>();
			e.printStackTrace();
			map.put("boolean",false);
			map.put("msg",msg_code);
			return map;
			
		}
	
	}
	
	
//	private Annotation getAnnotation(String goa) {
//		FileInputStream fileIn;
//		FSTObjectInput in;
//		try {
//			fileIn = new FileInputStream(goa);
//			in = new FSTObjectInput(fileIn);
//	         // required !
//	        Annotation GOA = (Annotation) in.readObject();
//	        in.close();
//	        fileIn.close();
//	        return GOA;
//		} catch (IOException | ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//
//        
//        
//       
//	}
	
	private String ICauthor(int ic) {
		String a = new String();
		switch(ic) {
		case 0:
			a = "seco";
			break;
		case 1:
			a = "zhou";
			break;
		case 2:
			a = "sanchez";
			break;
		case 3:
			a = "mazandu";
			break;
		default:
			a = "mazandu";
			break;
		}
		
		return a;
	}
	public Map<String,Object> GSAnMethod(List<String> genesList,List<String> ontology,GlobalOntology go, Annotation GOAincom ,String ssMethod,
			int geneSupport,Map<String,Double> percentile, Set<String> termsInc) {
		
		

		Integer icSimilarity = 4;
		double tailmin = 0.;
		double covering = 1.;
		double simRepFilter = 0.5;
		int ic_inc = 3; // correspond to Mazandu IC
		int msg_code = 0;

		Map<String,Object> finalResult = new HashMap<>();

		try {


			Map<String,Integer> gs2gi = new HashMap<>();
			int count = 0;
			for(String g : genesList) {

				gs2gi.put(g,count);
				count++;
			}

	Map<String,Set<String>> gene2term = new HashMap<>();
			
			for(String t : termsInc) {
				InfoTerm it= go.allStringtoInfoTerm.get(t);
				//System.out.println(it.toName());
				for(String g : it.geneSet) {
					if(gene2term.containsKey(g)) {
						gene2term.get(g).add(t);
					}else {
						gene2term.put(g, new HashSet<>());
						gene2term.get(g).add(t);
					}
				}
				
			}

			
			log.debug("Computing GS2...");
			double gs2 = GS2(genesList, gene2term, go);
			finalResult.put("GeneSet", genesList);
			Set<String> annotatedGS = new HashSet<>();
			
			
			
			
			
			
			finalResult.put("GS2", gs2);
			//	finalResult.put("stats_beforeGSAN", descriptiveSecore(termsInc).toJSONString());

			go.getLocalInformation();

			Set<String> termsDic = new HashSet<>();
			for(String t :termsInc) {
				termsDic.add(t);
				termsDic.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);
				annotatedGS.addAll(go.allStringtoInfoTerm.get(t).geneSet);
				
			//	System.out.println(go.allStringtoInfoTerm.get(t).toName() +" "+go.allStringtoInfoTerm.get(t).geneSet);
			}
			
			finalResult.put("AnnotatedGeneSet", new ArrayList<String>(annotatedGS));
		
			List<Cluster> rep = new ArrayList<Cluster>();
			int error = 0;
			for(String ont : ontology) {
				//System.out.println("###  ont: "+ ont);
			List<String> listTerm = new LinkedList<String>(termsInc);
			listTerm.retainAll(go.allStringtoInfoTerm.get(ont).is_a.descendants);
			log.debug("Writing Similarity Matrix...");
			writeSimilarityMatrix wSS = new writeSimilarityMatrix(ssMethod);
			Map<String,Object> mSS = wSS.similarityMethod(go, listTerm, icSimilarity.intValue());
			
            AlgorithmRepresentative ar = new AlgorithmRepresentative(ic_inc, ont, mSS, "average",
					tailmin,simRepFilter,covering, geneSupport);
			rep.addAll(ar.run( go, listTerm,GOAincom,percentile));
			error = ar.errorMsg;
			//System.out.println("ATENTIION " + error);
			}
			
			if(rep.isEmpty()) {
					msg_code = error;
			
				
			}
			
			List<String> allRepresentatives = new ArrayList<>();
	
			Set<String> repRes = new HashSet<>(); 
			log.debug("Post-processing to remove linked terms.");
			Map<String,BitSet> term2genebs = new HashMap<>(); // Thats is created for the SCP.
			for(Cluster r :rep) {
				for(InfoTerm it : r.representatives) {
					if(it.termcombi.isEmpty()) {
						allRepresentatives.add(it.id);
						repRes.add(it.id);
						BitSet bs = new BitSet();
						for(String g : it.geneSet) {
							
							bs.set(gs2gi.get(g));
						}
						term2genebs.put(it.id, bs);

					}else {
						repRes.addAll(it.termcombi);
						for(String t : it.termcombi) {
							allRepresentatives.add(t);
							BitSet bs = new BitSet();
							for(String g : it.geneSet) {
								bs.set(gs2gi.get(g));
							}
							term2genebs.put(it.id, bs);
						}
					}

				}

			}
			
			Set<String> remove = new HashSet<>();
			
			List<String> repResList = new ArrayList<>(repRes);
			//System.out.println("MIRAR AQUI "+repResList.size());
			TransformDagToTree tdt = new TransformDagToTree(go);
		
			Map<String, List<String>> hierarchy = tdt.GetHierarchy(GOAincom, repResList, ic_inc);
//			for(String t : hierarchy.keySet()) {
//				System.out.println(go.allStringtoInfoTerm.get(t)  + " " + hierarchy.get(t));
//			}
			Map<String, Set<String>> mapTerm2genes = new HashMap<>();
			
			Set<String> allterms = new HashSet<>(hierarchy.keySet());
			
			for(String t : allterms) {
				mapTerm2genes.put(t, new HashSet<>(go.allStringtoInfoTerm.get(t).geneSet));
				
			}
			
			
			for(int i = 0; i<allRepresentatives.size();i++) {
				InfoTerm ti = go.allStringtoInfoTerm.get(allRepresentatives.get(i));
				for(int j= i+1; j<allRepresentatives.size();j++) {
					InfoTerm tj = go.allStringtoInfoTerm.get(allRepresentatives.get(j));
					if(ti.is_a.ancestors.contains(tj.id)) {
//						System.out.println("Esto es un test");
//						System.out.println(ti.toName() + " remove "+tj.toName());	
						remove.add(tj.id);
					}else if(tj.is_a.ancestors.contains(ti.id)) {
//						System.out.println(tj.toName() + " remove "+ti.toName());
							remove.add(ti.id);
						
					}
				}
			}

			for(String rem : remove) { 

				term2genebs.remove(rem);
				repRes.remove(rem);
				allRepresentatives.remove(rem);


			}
			remove.clear();

			for(int i = 0; i<allRepresentatives.size();i++) {
				InfoTerm ti = go.allStringtoInfoTerm.get(allRepresentatives.get(i));
				for(int j= i+1; j<allRepresentatives.size();j++) {
					InfoTerm tj = go.allStringtoInfoTerm.get(allRepresentatives.get(j));
					if(ti.part_of.parents.contains(tj.id)) {
						tj.geneSet.addAll(ti.geneSet);
						if(tj.geneSet.size()>ti.geneSet.size()) {
							remove.add(ti.id);
						}else {
							remove.add(tj.id);
						}
					}else if(tj.part_of.parents.contains(ti.id)) {
						ti.geneSet.addAll(tj.geneSet);
						if(ti.geneSet.size()>tj.geneSet.size()) {
							remove.add(tj.id);
						}else {
							remove.add(ti.id);
						}
					}
						
				}
			}
			
			

			for(String rem : remove) { 

				term2genebs.remove(rem);
				repRes.remove(rem);
				

			}
			
		
			if(!hierarchy.isEmpty()) {
			List<String> line1 = new ArrayList<String>(ontology);
			line1.retainAll(hierarchy.keySet());
			List<String> line2 = new ArrayList<String>();
			
			boolean bol = false;
			//System.out.println(hierarchy);
			while(bol==false) {
				for(String t : line1) {
					for(String t_ch : hierarchy.get(t)) {
						mapTerm2genes.get(t).removeAll(mapTerm2genes.get(t_ch));
						line2.add(t_ch);
					}
				}
				if(!line2.isEmpty()) {
					line1.clear();
					line1.addAll(line2);
					line2.clear();
				}else {
					bol =true;
				}
				
				
			}
			finalResult.put("tree", tdt.transform(hierarchy, ic_inc, mapTerm2genes));
			}
			
			List<String> scp =  SetCoverAaron.scp(term2genebs,go,ontology);
			Set<String> genesTest = new HashSet<>();
			for(String t: scp) {
//				System.out.println("Rep? " + go.allStringtoInfoTerm.get(t).toName());
				genesTest.addAll(go.allStringtoInfoTerm.get(t).geneSet);
			}
//			System.out.println(genesTest.size());
			finalResult.put("scp", scp);
			finalResult.put("Reduce", Math.floor((double)scp.size()/(double)termsInc.size()*100.)/100.);

			
			
			Map<String,Map<String, Object>> mapInfo = new HashMap<>();
//			System.out.println("All terms " + allterms.size());
			for(String t : allterms) {
				InfoTerm it = go.allStringtoInfoTerm.get(t);
				Map<String, Object> mapTerm = new HashMap<>();
				mapTerm.put("name", it.toName());
				mapTerm.put("IC", it.ICs.get(ic_inc));
				mapTerm.put("onto", onto2simpleName(go.allStringtoInfoTerm.get(it.top).toName()));
				mapTerm.put("geneSet", new ArrayList<String>(mapTerm2genes.get(t)));
				
				if(it.toString().equals(it.top)) {
					mapTerm.put("parent", "GO");
				}else {
					List<String> p = new ArrayList<>(it.is_a.parents);
					p.retainAll(allterms);
				mapTerm.put("parent", p);
				}
				
				if(scp.contains(t)) {
					mapTerm.put("opacity", 1.);
				}else {
					boolean oui = false;
					
					for(String s : scp) {
						if(go.allStringtoInfoTerm.get(t).is_a.descendants.contains(s)) {
							oui = true;
							break;
						}
					}
					
					if(oui) {
						mapTerm.put("opacity", 1.);	
					}else {
						mapTerm.put("opacity", 1.);
					}
					
				}
				List<String> c = new ArrayList<>(it.is_a.childrens);
				c.retainAll(allterms);
				mapTerm.put("children", c);
				mapInfo.put(t, mapTerm);


			}
			
			
				Map<String, Object> mapTerm = new HashMap<>();
				mapTerm.put("name", "Gene Ontology");
				mapTerm.put("IC", 0);
				mapTerm.put("geneSet", new ArrayList<String>());
				mapTerm.put("children", ontology);
				mapTerm.put("opacity", 1.);
				mapInfo.put("GO", mapTerm);
			
			
			
		
			
			finalResult.put("representatives",new ArrayList<String>(repRes));
			
			
			Map<String,Set<String>> gene2rep = new HashMap<>();
			
			for(String r : repRes) {
				InfoTerm itr = go.allStringtoInfoTerm.get(r);
				for(String g : itr.geneSet) {
					if(gene2rep.containsKey(g)) {
						gene2rep.get(g).add(r);
					}else {
						gene2rep.put(g, new HashSet<>());
						gene2rep.get(g).add(r);
					}
				}
				
			}
			

			finalResult.put("terms", mapInfo);
			log.debug("Computing Synthetic algorithm...");
			
			Map<String,Object> map = new HashMap<>(finalResult);
			map.put("msg", msg_code);
			map.put("boolean", true);
			return map;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(""+ e);
			Map<String,Object> map = new HashMap<>();
			map.put("msg", msg_code);
			map.put("boolean", false);
			return map;
		}

		

	}
	
	
	public Set<InfoTerm> getAllInformation(GlobalOntology go,Set<String> termsInc){
		Set<InfoTerm> set = new HashSet<>();
		for(String t : termsInc ) {
			set.add(go.allStringtoInfoTerm.get(t));
			for(String anc : go.allStringtoInfoTerm.get(t).is_a.ancestors) {
				set.add(go.allStringtoInfoTerm.get(anc));
			}
		}
		return set;
	}

	public static double GS2(List<String> symbols,Map<String,Set<String>> gene2rep, GlobalOntology go) {

		Map<String, Set<String>> map = new HashMap<>();
		List<String> genePresent = new ArrayList<>();
		for(String s : symbols) {
			if(gene2rep.containsKey(s)) {
				genePresent.add(s);
				for(String t : gene2rep.get(s)) {
					if(!map.containsKey(t)) {
						map.put(t, new HashSet<>());
						map.get(t).add(s);
					}else {
						map.get(t).add(s);
					}
					for(String anc : go.allStringtoInfoTerm.get(t).is_a.ancestors) {
						if(!map.containsKey(anc)) {
							map.put(anc, new HashSet<>());
							map.get(anc).add(s);
						}else {
							map.get(anc).add(s);
						}
					}
				}
			}
		}
		int H = genePresent.size()-1;
		double dd = 0.;
		for(String s : genePresent) {
			int ng = gene2rep.get(s).size();
			double sum = 0.;
			for(String t : gene2rep.get(s)) {
				List<String> lt = new ArrayList<String>();
				lt.add(t);
				lt.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);
				int san = lt.size();
				double rank = 0.;
				for(String anc : lt) {
					Set<String> set = new HashSet<>(map.get(anc));
					set.remove(s);
					rank = rank + ((double)set.size()/(double)H);

				}
				sum = sum + (1/(double)san) * rank;

			}
			if(Double.isNaN(dd)) {
				System.exit(0);
			}
			dd = dd + (1./(double)ng) * sum;
		}

		return (1/(double)genePresent.size()) * dd;
	}
	
	public String onto2simpleName(String onto) {
		String t = new String();
		switch (onto) {
		case "biological_process":
			t = "BP";
			break;
		case "molecular_function":
			t =  "MF";
			break;
		case "cellular_component":
			t= "CC";
			break;
		case "reactome":
			t= "reac";
			break;
		default:
			t="other";
			break;
		}
		return t;
	}

}
