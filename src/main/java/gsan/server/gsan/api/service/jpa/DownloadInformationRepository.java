package gsan.server.gsan.api.service.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import gsan.server.gsan.api.service.model.DownloadInformation;


public interface DownloadInformationRepository extends JpaRepository<DownloadInformation, Long> {

}
