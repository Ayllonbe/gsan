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
			graphSingleton.initializeOrGet("src/main/resources/static/ontology/go.owl");
		
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
	
	

}

