package gsan.server.gsan.api.service;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

 
public interface taskRepository extends JpaRepository<task, UUID>{
	
	
}
