package gsan.server.gsan.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
			FTPDownloader.DownloadGOOWL(GOOWL); // Commenter pour l'instance mais important
		}

		
		
		graphSingleton.initializeOrGet(path+GOOWL);
		FTPDownloader.DownloadGOA();
		
		System.out.println("Ready to use!");

	}
	

	

}
