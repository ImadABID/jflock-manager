package fr.abied.jflock.manager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@DirtiesContext
class WorkManagerHiringTest {

    @TestConfiguration
    static class SpringConfiguration {
    
        @Bean
        WorkDescription<WorkAssignmentExample, WorkSubmissionExample> workDescription(){

            @SuppressWarnings("unchecked")
            WorkDescription<WorkAssignmentExample, WorkSubmissionExample> workDescription = mock(WorkDescription.class);

            when(workDescription.getPackageName()).thenReturn("the.only.work.type");
            when(workDescription.getWorkAssignmentFluxProxy())
                .thenReturn(Flux.just(new WorkAssignment("the.only.work.type", "{\"msg\":\"1st assignment\"}")));

            return workDescription;
        }

    }
    
    
    @Autowired
    private WorkManager workManager;

    @Test
    void testOneWorkerOneAssignment(){
        
        assertNotNull(this.workManager);

        Flux <WorkSubmission> submissionFlux = Flux
            .just(
                new WorkSubmission("the.only.work.type", "", null),
                new WorkSubmission("the.only.work.type", "", null))
            .delayElements(Duration.ofSeconds(1));


        Flux<WorkAssignment> assignmentFlux = workManager.hiring(submissionFlux);

        StepVerifier.create(assignmentFlux.take(1))
            .expectNextMatches(wa ->  "{\"msg\":\"1st assignment\"}".equals(wa.getPayload()))
            .verifyComplete();
        
    }

}
