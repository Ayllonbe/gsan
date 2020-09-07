package gsan.server.gsan.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gsan.server.gsan.api.service.enumerations.CustomException;
import gsan.server.gsan.api.service.jpa.taskRepository;
import gsan.server.gsan.api.service.model.task;
import gsan.server.singleton.graphSingleton;

@Component
public class ScheduledTasks {

	private static final SimpleDateFormat dateJSONFormat = new SimpleDateFormat(
			"HH");
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/*
	 * Download GO and GOA. Scheduled of 24h.
	 */

	//@Scheduled(cron="0 0 12 * * ?")
	//@Scheduled(cron="0 0 * * 1 *")
	
	@Scheduled(cron="0 0 12 * * MON")
	public void performTask() throws IOException {
		
		FTPDownloader.DownloadGOA();
		
		boolean cond = false;
		try {
			cond = FTPDownloader.DownloadGOOWL("go.owl","http://current.geneontology.org/ontology/","src/main/resources/static/ontology/");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(cond)
<<<<<<< HEAD
			graphSingleton.initializeOrGet("go.owl");
=======
			graphSingleton.initializeOrGet("src/main/resources/static/ontology/go.owl");
>>>>>>> Release_1.0.1
		
		log.debug("Checking and Donwloading GO and GOA.");

	}

	/*
	 * Remove The JSON file with more than 12h. Scheduled of 24h.
	 */
	@Scheduled(cron="0 */12 * * * *")
	public void RemoveOLDJSON() throws IOException {
		String repertoire = "src/main/tmp/results";
		File dir  = new File(repertoire);
		if(dir.isDirectory()){
	         File s[] = dir.listFiles();
	         for (File f : s){
	           int time = Integer.parseInt(dateJSONFormat.format(TimeStamp.getCurrentTime().getTime()- f.lastModified()));
	           if(time>=12) {
	        	   log.debug("The file "+f.getName()+ " is removed.");
	        	   f.delete();
	           }
	         
	         }
	 }

	}
	
	@Autowired 
	private taskRepository tRepository;
	@Autowired
	private JavaMailSender sender;

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	DateTimeFormatter day = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter heure = DateTimeFormatter.ofPattern("HH");
//	@Scheduled(cron="0 0 * * 5 *")
	@Scheduled(cron="0 0 12 * * WED")
	
	//@Scheduled(cron="0 0/5 * * * *")
	public void reports() {
		System.out.println("hola");
		Map<Integer,Set<UUID>> mapError2Users = new HashMap<>();
		Map<String,Set<UUID>> mapDay2Users = new HashMap<>();
		
		if(tRepository.findAll().size()>0) {
			for(task t : tRepository.findAll()) {
				if(mapError2Users.containsKey(t.getMSGError())) {
					mapError2Users.get(t.getMSGError()).add(t.getId());
				}else {
					mapError2Users.put(t.getMSGError(),new HashSet<>());
					mapError2Users.get(t.getMSGError()).add(t.getId());
				}
				String d = t.getDate().toLocalDateTime().format(day);
				if(mapDay2Users.containsKey(d)) {
					mapDay2Users.get(d).add(t.getId());
				}else {
					mapDay2Users.put(d, new HashSet<>());
						mapDay2Users.get(d).add(t.getId());
					}
				
				String repertoire = "src/main/tmp/results/"+t.getId()+".json";
				File f = new File(repertoire);
		           if(!f.exists()) {
		        	   log.debug("The taks "+t.getId()+ " is removed.");
		        	   tRepository.delete(t);
		           }
				
				}
			StringBuffer sb = new StringBuffer();
			sb.append("Repport\n");
			for(Integer i : mapError2Users.keySet()) {
				String E = CustomException.values()[i].getError();
				String msjE = 	CustomException.values()[i].getMSG();	
				
				sb.append(E+"\t"+msjE+"\t"+mapError2Users.get(i).size()+"\n");

			
			}
			for(String i : mapDay2Users.keySet()) {
				
				sb.append(i+"\t"+mapDay2Users.get(i).size()+"\n");

			
			}
			
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			// Set From: header field of the header.
			try {
				helper.setFrom(new InternetAddress("no-reply-gsan@labri.fr", "NO-REPLY"));
				// Set To: header field of the header.
				helper.setTo(new InternetAddress("ayllonbenitez.aaron@gmail.com"));
				// Set Subject: header field
				message.setSubject("[GSAn] Repport");
				
				// Fill the message
				helper.setText(sb.toString());

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("The email is incorrect");
				//e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				System.out.println("The email is incorrect");
				//e.printStackTrace();
			}
			sender.send(message);
			System.out.println("Sent message successfully....");
			
			
		}
	}
	

}
