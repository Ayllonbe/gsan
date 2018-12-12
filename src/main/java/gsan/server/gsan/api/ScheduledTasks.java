package gsan.server.gsan.api;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gsan.server.gsan.api.service.jpa.IntegrationSourcesRepository;
import gsan.server.gsan.api.service.model.IntegrationSource;
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
	@Scheduled(cron="0 0 0 * * ?")
	public void performTask() throws IOException {
		
		FTPDownloader.DownloadGOA();
		boolean cond = FTPDownloader.DownloadGOOWL("go.owl");
		if(cond)
			graphSingleton.initializeOrGet("go.owl");
		log.debug("Checking and Donwloading GO and GOA.");

	}

	/*
	 * Remove The JSON file with more than 12h. Scheduled of 24h.
	 */
	@Scheduled(cron="0 */12 * * * ?")
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
	private IntegrationSourcesRepository integrationRepository;
	@Scheduled(cron="*/15 * * * * ?")
	public void CheckGoInDB() throws IOException {
		List<IntegrationSource> listis = integrationRepository.findAll();
		for(IntegrationSource is:listis) {
			System.out.println(is.getId() +"\t" +is.getName() +"\t" +is.getfilename());
		}
	}
}
