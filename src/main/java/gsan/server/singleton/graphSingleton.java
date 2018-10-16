package gsan.server.singleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.integration.GlobalGraph;
import gsan.server.gsan.api.FTPDownloader;
import gsan.server.gsan.api.service.jpa.DownloadInformationRepository;
import gsan.server.gsan.api.service.jpa.IntegrationSourcesRepository;
import gsan.server.gsan.api.service.model.DownloadInformation;
import gsan.server.gsan.api.service.model.IntegrationSource;

public class graphSingleton {
	
	private static GlobalOntology goBase;


		
public static void initializeOrGet(String GOOWL) {
	
		File owlf = new File(GOOWL);
		
		if(owlf.exists()) {
			System.out.println(owlf.getAbsolutePath());
		}else {
			System.out.println("The file don't exist");
		}
		
		// Charge ontology, resoner ontology and recover information
		GlobalOntology go= GlobalOntology.informationOnt(owlf.getAbsolutePath());
		/*
		 * INIT TEST HUMAN integration
		 */
		File pathways = new File("src/main/resources/static/integration/ReactomePathways.txt");
		File rel = new File("src/main/resources/static/integration/ReactomePathwaysRelation.txt");
		GlobalOntology reactome = GlobalGraph.getGraph(pathways, rel);
		go.allStringtoInfoTerm.putAll(reactome.allStringtoInfoTerm);
		
		for(String author : go.IC2DS.keySet()) {
			go.IC2DS.get(author).putAll(reactome.IC2DS.get(author));
			
		}
		
		
		go.subontology.putAll(reactome.subontology);
		
		
		
		String doid = "src/main/resources/static/integration/doid.owl";
		
		GlobalOntology DO =GlobalOntology.informationOnt(doid);
		go.allStringtoInfoTerm.putAll(DO.allStringtoInfoTerm);
		
		for(String author : go.IC2DS.keySet()) {
			go.IC2DS.get(author).putAll(DO.IC2DS.get(author));
			
		}
		go.subontology.putAll(DO.subontology);
		
		/*
		 * END Test
		 */
		goBase = go;
		
		
	}
	public static GlobalOntology getGraph(boolean prok) {
		
		if(prok) {
			
			GlobalOntology go = new GlobalOntology(goBase);
			//go.prokaryoteOnto(); // Gene Ontology remove the prokaryote subset so I remove that.
			return go;
			
		}else {
		
		return new GlobalOntology(goBase);
		}
	}
	
	
}
