package gsan.server.gsan.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//import it.ozimov.springboot.mail.configuration.EnableEmailTools;

@SpringBootApplication
@EnableAsync
//@EnableEmailTools
@EnableScheduling
public class Application {

	@Value("${pool.size:3}")
	private int poolSize;;

//	@Value("${queue.capacity:1000}")
//	private int queueCapacity;

	@Bean(name="workExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(poolSize);
		//taskExecutor.setQueueCapacity(queueCapacity);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
