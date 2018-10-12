package gsan.server.gsan.api.service;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface GSAnService {
	
	
	
	
	void runService(taskRepository tR, task t,List<String> query, String organism, boolean IEA,int inc,List<String> ontology,
			String ssMethod, double simRepFilter, double covering,
			int geneSupport, int percentile,boolean prok);
	void runService(taskRepository tR, task t,List<String> query, File goaf, boolean IEA,int ic_inc,List<String> ontology,
			String ssMethod, double simRepFilter, double covering,
			int geneSupport, int percentile, boolean prok);
	Map<String,Object> runService(List<String> query, String organism, boolean IEA,int inc,List<String> ontology,
			String ssMethod, double simRepFilter, double covering,
			int geneSupport, int percentile, boolean prok); 
	
}
