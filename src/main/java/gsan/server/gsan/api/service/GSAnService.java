package gsan.server.gsan.api.service;

import java.util.List;
import java.util.Map;
import gsan.server.gsan.api.service.jpa.taskRepository;
import gsan.server.gsan.api.service.model.task;

public interface GSAnService {




	void runService(taskRepository tR, task t,List<String> query, String organism, boolean IEA,
			List<String> ontology, String ssMethod, int geneSupport,int percentile,int ids);
	void runService(taskRepository tR, task t,List<String> query, boolean IEA,List<String> ontology,
			String ssMethod, int geneSupport, int percentile, String goaf,int ids);
	Map<String,Object> runService(List<String> query, String organism, boolean IEA,List<String> ontology,
			String ssMethod, int geneSupport, int percentile,int ids);

}
