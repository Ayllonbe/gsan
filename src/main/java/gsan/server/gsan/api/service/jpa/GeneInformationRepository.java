package gsan.server.gsan.api.service.jpa;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gsan.server.gsan.api.service.model.GeneInformation;

public interface GeneInformationRepository extends JpaRepository<GeneInformation, Long> {
	 List<GeneInformation> findByTaxon(int taxon);
}
