package gsan.server.gsan.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import gsan.server.singleton.graphSingleton;

@Component
public class ComplementStartUp  implements ApplicationListener<ApplicationReadyEvent>{
	private static String GOOWL = "go.owl";
	private static String path = "src/main/resources/static/ontology/";
	

	
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
	
		Path dir = Paths.get("src/main/tmp/results/");
		if(dir.toFile().exists()) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
		    for (Path file : stream) {
		    	Files.delete(file);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else {
			dir.toFile().mkdirs();
		}
		 // Commenter pour l'instance, mais important
		File goFile = new File(path+GOOWL);
		if(!goFile.exists()) {
			try {
				FTPDownloader.DownloadGOOWL(GOOWL,"http://current.geneontology.org/ontology/",path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Commenter pour l'instance mais important
		}

		
		graphSingleton.initializeOrGet(path+GOOWL);
		
		
		
		File annotations = new File("src/main/resources/static/AssociationTAB");
		if(!annotations.exists()) {
		FTPDownloader.DownloadGOA();
		}
		String[] listFiles = new String[] {"ecocyc.gaf","cgd.gaf","goa_human.gaf","goa_arabidopsis.gaf","goa_chicken.gaf","goa_cow.gaf","goa_dog.gaf",
				"goa_fly.gaf","goa_mouse.gaf","goa_pig.gaf","goa_rat.gaf","goa_worm.gaf","goa_yeast.gaf","goa_zebrafish.gaf"
		};
		for(String sf : listFiles) {
			File f = new File("src/main/resources/static/AssociationTAB/"+sf);
			if(!f.exists()) {
				FTPDownloader.DownloadGOA();
			}
		}

		System.out.println("Ready to use!");
		try {
			FTPDownloader.DownloadGOOWL(GOOWL,"http://current.geneontology.org/ontology/",path);
//			FTPDownloader.DownloadGOA();//NO OLVIDAR!!!!!
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println("Ready to use!");
	}
	
	

}
