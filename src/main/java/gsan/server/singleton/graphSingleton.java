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
