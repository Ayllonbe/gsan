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
	private static String GOOWL = "src/main/resources/static/ontology/go.owl";
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		// debug
		//System.out.println("Removing the json...");
		Path dir = Paths.get("src/main/tmp/results/");

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
		    for (Path file : stream) {
		    	Files.delete(file);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 // Commenter pour l'instance, mais important
		File goFile = new File(GOOWL);
		if(!goFile.exists()) {
			FTPDownloader.DownloadGOOWL(GOOWL); // Commenter pour l'instance mais important
		}
		graphSingleton.initializeOrGet(GOOWL);
		graphSingleton.getGraph(false);
		
//		System.out.println("Downloading GOA!");
		FTPDownloader.DownloadGOA();
		
		System.out.println("Ready to use!");
		
		
		
//		StringBuffer sb = new StringBuffer();
//		sb.append("ID\tTerm\tLevel\tICSeco\tICZhou\tICSanchez\tICGOUniversal\n");
//		
//		for(String t : go.allStringtoInfoTerm.keySet()) {
//			
//			sb.append(t+"\t"+ go.allStringtoInfoTerm.get(t).toName()
//					+"\t"+go.allStringtoInfoTerm.get(t).depth()
//					+"\t"+go.allStringtoInfoTerm.get(t).ICs.get(0)
//					+"\t"+go.allStringtoInfoTerm.get(t).ICs.get(1)
//					+"\t"+go.allStringtoInfoTerm.get(t).ICs.get(2)
//					+"\t"+go.allStringtoInfoTerm.get(t).ICs.get(3)
//					+"\n");
//			
//		}
//		try {
//			PrintWriter pw = new PrintWriter("src/main/resources/static/ontology/goInfo.tsv");
//			pw.print(sb.toString());
//			pw.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		StringBuffer sb_isA = new StringBuffer();
//		StringBuffer sb_partOf = new StringBuffer();
//		StringBuffer sb_reg = new StringBuffer();
//		sb_isA.append("From\tTo\n");
//		sb_partOf.append("From\tTo\n");
//		sb_reg.append("From\tTo\n");
//		
//		for(String t : go.allStringtoInfoTerm.keySet()) {
//			
//			for(String p : go.allStringtoInfoTerm.get(t).is_a.parents) {
//				sb_isA.append(t+"\t"+p+"\n");
//			}
//			if(!go.allStringtoInfoTerm.get(t).part_of.parents.isEmpty()) {
//				for(String p : go.allStringtoInfoTerm.get(t).part_of.parents) {
//					sb_partOf.append(t+"\t"+p+"\n");
//				}
//			}
//			if(go.allStringtoInfoTerm.get(t).getRegulatesClass()!=null) {
//				sb_reg.append(t+"\t"+go.allStringtoInfoTerm.get(t).getRegulatesClass()+"\n");
//			}
//		}
//		try {
//			PrintWriter pw = new PrintWriter("src/main/resources/static/ontology/isA.tsv");
//			pw.print(sb_isA.toString());
//			pw.close();
//			pw = new PrintWriter("src/main/resources/static/ontology/partOf.tsv");
//			pw.print(sb_partOf.toString());
//			pw.close();
//			pw = new PrintWriter("src/main/resources/static/ontology/reg.tsv");
//			pw.print(sb_reg.toString());
//			pw.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
