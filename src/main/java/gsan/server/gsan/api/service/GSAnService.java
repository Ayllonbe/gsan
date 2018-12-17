package gsan.server.gsan.api.service;

import java.util.List;
import java.util.Map;
import gsan.server.gsan.api.service.jpa.taskRepository;
import gsan.server.gsan.api.service.model.task;

public interface GSAnService {
	
	
	
	
	void runService(taskRepository tR, task t,List<String> query, String organism, boolean IEA,int inc,List<String> ontology,
			String ssMethod, double simRepFilter, double covering,
			int geneSupport, int percentile,boolean prok,int ids);
	void runService(taskRepository tR, task t,List<String> query, boolean IEA,int ic_inc,List<String> ontology,
			String ssMethod, double simRepFilter, double covering,
			int geneSupport, int percentile, boolean prok, String goaf,int ids);
	Map<String,Object> runService(List<String> query, String organism, boolean IEA,int inc,List<String> ontology,
			String ssMethod, double simRepFilter, double covering,
			int geneSupport, int percentile, boolean prok,int ids); 
	
}
