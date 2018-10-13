package gsan.server.gsan.api.service.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gsan.server.gsan.api.service.model.AnnotationSource;

public interface AnnotationSourcesRepository  extends JpaRepository<AnnotationSource, Long>{
	List<AnnotationSource> findByIntegrationSourceId(Long integrationsourceId);	
	List<AnnotationSource> findByDownloadInformationId(Long downloadinformationId);	
}
