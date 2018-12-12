package gsan.server.gsan.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import gsan.server.gsan.api.service.jpa.DownloadInformationRepository;
import gsan.server.gsan.api.service.jpa.IntegrationSourcesRepository;
import gsan.server.gsan.api.service.model.DownloadInformation;
import gsan.server.gsan.api.service.model.IntegrationSource;
import gsan.server.singleton.graphSingleton;
//import it.ozimov.springboot.mail.model.Email;
//import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
//import it.ozimov.springboot.mail.service.EmailService;

@Component
public class ComplementStartUp  implements ApplicationListener<ApplicationReadyEvent>{
	private static String GOOWL = "go.owl";
	private static String path = "src/main/resources/static/ontology/";
	
	@Autowired
	private IntegrationSourcesRepository integrationRepository;
	@Autowired
	private DownloadInformationRepository downloadRepository;
	
	

//	public void sendEmailWithoutTemplating() throws UnsupportedEncodingException, MessagingException{
//		 
//		 Properties properties = System.getProperties();
//		
////		 Setup mail server
//	      properties.setProperty("mail.smtp.host","smtpauth.u-bordeaux.fr");
//	      properties.setProperty("mail.smtp.port","465");
//	      properties.setProperty("mail.smtp.user", "securite@labri.fr");
//	      properties.setProperty("mail.smtp.ssl.enable", "true");
//	      properties.setProperty("mail.smtp.timeout", "5000");
//	      Session session = Session.getDefaultInstance(properties);
//	      
//	      MimeMessage message = new MimeMessage(session);
//
//	         // Set From: header field of the header.
//	         message.setFrom(new InternetAddress("gsan@labri.fr"));
//
//	         // Set To: header field of the header.
//	         message.addRecipient(Message.RecipientType.TO,new InternetAddress("ayllonbenitez.aaron@gmail.com"));
//
//	         // Set Subject: header field
//	         message.setSubject("This is the Subject Line!");
//
//	         // Create the message part 
//	         BodyPart messageBodyPart = new MimeBodyPart();
//
//	         // Fill the message
//	         messageBodyPart.setText("This is message body");
////	         
//	         // Create a multipar message
//	         Multipart multipart = new MimeMultipart();
//
//	         // Set text message part
//	         multipart.addBodyPart(messageBodyPart);
//
//	         // Part two is attachment
//	         
//	         multipart.addBodyPart(messageBodyPart);
//
//	         // Send the complete message parts
//	         message.setContent(multipart );
//
//	         // Send message
//	         Transport.send(message);
//	         System.out.println("Sent message successfully....");
//	      // Get the default Session object.
//	}

	
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		// debug
		//System.out.println("Removing the json...");
//		
//		try {
//			sendEmailWithoutTemplating();
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
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
//		
//		try {
//			initSourcesDB();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		
		
		
		graphSingleton.initializeOrGet(path+GOOWL);
		
		//graphSingleton.getGraph(false);
		
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
	

	@SuppressWarnings("resource")
	public void initSourcesDB() throws IOException {
		List<DownloadInformation> listdi = 	downloadRepository.findAll();
		List<String> pathways = new ArrayList<>();
		List<Long> pathids = new ArrayList<>();
		for(DownloadInformation di : listdi) {
			String p = di.getUri()+"/"+di.getPath();
			pathways.add(p);
			pathids.add(di.getId());
		}
		File ontologiesfile = new File("src/main/resources/static/ontology/knowledgeSource.txt");
		InputStream inputStream = new FileInputStream(ontologiesfile);			 

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String output = bufferedReader.readLine();
		//List<IntegrationSource> listis = new ArrayList<>();
		while (output != null) {

			System.out.println(output);
			 
			String[] col = output.split("\t");
			
			String name = col[0];
			String filename = col[1];
			String anexfilename = col[2];
			boolean iscompresed = Boolean.parseBoolean(col[5]);
			String type = col[3];
			String uri = col[4];
			String path = col[6];
			String auth = col[7];
			String ps;
			if(col.length>7) {
				ps = "";
			}else {
				System.out.println(col.length);
				for(String c : col) System.out.println(c);
				ps = col[8];
			}
			
			if( anexfilename!="") {
				DownloadInformation di = getDownloadInfo(pathids, pathways, uri, path, type, auth, ps);
				IntegrationSource is = new IntegrationSource(name,filename,iscompresed);
				is.setDownloadInformation(di);
				 boolean check = FTPDownloader.DownloadOntology(is);
				 if(check) {
					 downloadRepository.save(di);
					 integrationRepository.save(is);
				 }
				//listis.add(is);
			}else {
				
				DownloadInformation di = getDownloadInfo(pathids, pathways, uri, path, type, auth, ps);
				IntegrationSource is = new IntegrationSource(name,filename,iscompresed);
				is.setDownloadInformation(di);
				 boolean check = FTPDownloader.DownloadOntology(is);
				 if(check) {
					 downloadRepository.save(di);
					 integrationRepository.save(is);
				 }
				//listis.add(is);
			}
			output = bufferedReader.readLine();
		}
	}
	
	public  DownloadInformation getDownloadInfo(List<Long> ids, List<String> pathways,
			String uri,String path, String type, String auth, String ps) {
		
		if(pathways.contains(uri+"/"+path)) {
			return downloadRepository.getOne(ids.get(pathways.indexOf(path)));
		}else{
			DownloadInformation di = new DownloadInformation(uri, path, auth, ps, type);
			
			return di;
		}
	

	}
	public List<IntegrationSource> getIntegrationSourcesList() {
		return integrationRepository.findAll();
	}

}
