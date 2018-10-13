package gsan.server.gsan.api.service.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gsan.server.gsan.api.service.model.AnnotationSource;
import gsan.server.gsan.api.service.model.IntegrationSource;

public interface IntegrationSourcesRepository extends JpaRepository<IntegrationSource, Long>{

	List<AnnotationSource> findByDownloadInformationId(Long downloadinformationId);	
}
