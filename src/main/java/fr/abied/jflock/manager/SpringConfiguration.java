package fr.abied.jflock.manager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class SpringConfiguration {
    @Bean
    WorkDispatcher workDispatcher(WorkManager workManager){
        return new WorkDispatcher(workManager);
    }
}
