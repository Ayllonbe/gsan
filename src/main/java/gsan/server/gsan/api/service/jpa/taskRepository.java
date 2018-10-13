package gsan.server.gsan.api.service.jpa;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import gsan.server.gsan.api.service.model.task;

 
public interface taskRepository extends JpaRepository<task, UUID>{
	
	
}
