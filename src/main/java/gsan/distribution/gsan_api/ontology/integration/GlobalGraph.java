package gsan.distribution.gsan_api.ontology.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import gsan.distribution.gsan_api.ontology.OntoInfo;

public class GlobalGraph{

	public Map<String,InfoTerm> allStringtoInfoTerm;
	public Map<String,DescriptiveStatistics> IC2DS;
	public Map<String,OntoInfo> subontology;

//	public static void main(String[] args) throws IOException {
//		File pathways = new File("src/main/resources/static/ontology/reactome/ReactomePathways.txt");
//		File rel = new File("src/main/resources/static/ontology/reactome/ReactomePathwaysRelation.txt");
//		GlobalOntology reactome = getGraph(pathways, rel);
//		List<List<String>> goaTable;
//		String author = ICauthor(3);
//		goaTable = ReadFile.ReadAnnotation(goaf.getAbsolutePath());
//		Annotation GOA = new Annotation(goaTable, reactome,true,author,25);
//		
//		reactome.AddTermToGenome(GOA);	
//		reactome.addAnnotationICs();
//		reactome.AggregateIC();
//		Annotation  GOAred = Annotation.redondancyReduction(GOA,reactome);
//		Annotation GOAincom = Annotation.icIncompleteReduction(GOAred,reactome,3);
//		
//		
//		for(String t : reactome.allStringtoInfoTerm.keySet()) {
//			System.out.println(reactome.allStringtoInfoTerm.get(t).top);
//			break;
//		}
//
//	}

	public static String ICauthor(int ic) {
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
	public static GlobalOntology getGraph(File pathways, File rel, String source) {
		
		//		File pathways = new File("src/main/resources/static/ontology/reactome/ReactomePathways.txt");
		//		File rel = new File("src/main/resources/static/ontology/reactome/ReactomePathwaysRelation.txt");

		Map<String,String> map = new HashMap<>();
		Map<String,Set<String>> child2parent = new HashMap<>();
		Map<String,Set<String>> parent2child = new HashMap<>();

		try {
			List<String> lines = Files.readAllLines(pathways.toPath());
			for(String l : lines) {
				String[] line = l.split("\t");
				map.put(line[0], line[1]);
			}

			lines.clear();
			lines = Files.readAllLines(rel.toPath());
			for(String l : lines) {
				String[] line = l.split("\t");
				// There is no error. The file is a parent2children.
				if(child2parent.containsKey(line[1])) {
					child2parent.get(line[1]).add(line[0]);	
				}else {
					child2parent.put(line[1], new HashSet<String>());
					child2parent.get(line[1]).add(line[0]);
				}
				if(parent2child.containsKey(line[0])) {
					parent2child.get(line[0]).add(line[1]);	
				}else {
					parent2child.put(line[0], new HashSet<String>());
					parent2child.get(line[0]).add(line[1]);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(String t : map.keySet()) {
			if(!child2parent.containsKey(t)){
				child2parent.put(t, new HashSet<String>());
				child2parent.get(t).add("reac");
				if(parent2child.containsKey("reac")) {
					parent2child.get("reac").add(t);	
				}else {
					parent2child.put("reac", new HashSet<String>());
					parent2child.get("reac").add(t);
				}

			}
			if(!parent2child.containsKey(t)) {
				parent2child.put(t, new HashSet<String>());
			}
		}
		map.put("reac", "reactome");
		child2parent.put("reac", new HashSet<String>());
		
		return new GlobalOntology(map, parent2child, child2parent,source);

	}
}
