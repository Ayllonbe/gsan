package gsan.server.singleton;

import java.io.File;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
public class graphSingleton {
	
	private static GlobalOntology goBase;


		
public static void initializeOrGet(String GOOWL) {
	
		File owlf = new File(GOOWL);
		
	 	if(owlf.exists()) {
			System.out.println(owlf.getAbsolutePath());
		}else {
			System.out.println("The file don't exist");
		}
		String[] args = new String[2];
		args[0] = owlf.getAbsolutePath();
		args[1] = "GO";
		// Charge ontology, resoner ontology and recover information
		GlobalOntology go= GlobalOntology.informationOnt(args);
		/*
		 * INIT TEST HUMAN integration
		 */
//		File pathways = new File("src/main/resources/static/integration/ReactomePathways.txt");
//		File rel = new File("src/main/resources/static/integration/ReactomePathwaysRelation.txt");
//		String reac = "R";
//		GlobalOntology reactome = GlobalGraph.getGraph(pathways, rel,reac);
//		go.allStringtoInfoTerm.putAll(reactome.allStringtoInfoTerm);
//		
//		for(String author : go.IC2DS.keySet()) {
//			go.IC2DS.get(author).putAll(reactome.IC2DS.get(author));
//			
//		}
//		
//		
//		go.subontology.putAll(reactome.subontology);
//		go.sourceSet.addAll(reactome.sourceSet);
//		
//		
//		String doid = "src/main/resources/static/integration/doid.owl";
//		args[0] = doid;
//		args[1] = "DO";
//		GlobalOntology DO =GlobalOntology.informationOnt(args);
//		go.allStringtoInfoTerm.putAll(DO.allStringtoInfoTerm);
//		
//		for(String author : go.IC2DS.keySet()) {
//			go.IC2DS.get(author).putAll(DO.IC2DS.get(author));
//			
//		}
//		go.subontology.putAll(DO.subontology);
//		go.sourceSet.addAll(DO.sourceSet);
		
//		for(String sub : go.subontology.keySet()) {
//			OntoInfo oi = go.subontology.get(sub);
//			
//			System.out.println(go.allStringtoInfoTerm.get(sub).toName() +" " +oi.numberOfNodes() +" "+oi.numberOfEdges()+" "+oi.maxDepth());
//			
//		}
		/*
		 * END Test
		 */
		goBase = go;

	for(String t : go.allStringtoInfoTerm.keySet()) {
		if(go.allStringtoInfoTerm.get(t).ICs.get(3)>9352.84) {
			System.out.println(go.allStringtoInfoTerm.get(t).toName()+" "+go.allStringtoInfoTerm.get(t).ICs.get(3));
		}
	}
		
//		System.out.println(go.getPercentile(25, "mazandu", "GO"));
//		System.out.println(go.getPercentile(50, "mazandu", "GO"));
//		System.out.println(go.getPercentile(75, "mazandu", "GO"));
		
	}
	public static GlobalOntology getGraph() {
		
		return new GlobalOntology(goBase);
		
	}
	
	
}
