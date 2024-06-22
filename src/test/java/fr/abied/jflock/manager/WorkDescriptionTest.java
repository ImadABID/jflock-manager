package fr.abied.jflock.manager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class WorkDescriptionTest {
    
    @Test
    void testSerializationProxyInvocation(){
        
        class WorkDescriptionExample extends WorkDescription<String, WorkSubmissionExample>{

            public WorkDescriptionExample() {
                super(String.class, WorkSubmissionExample.class);
            }
        
            @Override
            public String getPackageName() {
                return null;
            }

            @Override
            public void workSubmissionHandler(WorkSubmissionExample workSubmission) {
                assertEquals("some message", workSubmission.getMsg());
            }

            @Override
            public Flux<String> getWorkAssignmentFlux() {
                return Flux.empty();
            }
        
        }

        WorkDescription<String, WorkSubmissionExample> workDescription = new WorkDescriptionExample();

        assertDoesNotThrow(()->{
            workDescription.workSubmissionHandlerSerializationProxy(
                new WorkSubmission(null, "{\"msg\":\"some message\"}", null));
        });

        assertNotNull(workDescription.getWorkAssignmentType());
        assertNotNull(workDescription.getWorkSubmissionType());

    }

    @Test
    void testGetWorkAssignmentFluxProxy(){

        class WorkDescriptionExample extends WorkDescription<WorkAssignmentExample, String>{

            public WorkDescriptionExample() {
                super(WorkAssignmentExample.class, String.class);
            }
        
            @Override
            public String getPackageName() {
                return null;
            }

            @Override
            public void workSubmissionHandler(String workSubmission) {
                // Not necessary for this test
            }

            @Override
            public Flux<WorkAssignmentExample> getWorkAssignmentFlux() {
                return Flux.just(new WorkAssignmentExample("1st assignment"));
            }
        
        }

        WorkDescription<WorkAssignmentExample, String> workDescription = new WorkDescriptionExample();

        StepVerifier.create(workDescription.getWorkAssignmentFluxProxy())
            .expectNextMatches(workAssignment -> "{\"msg\":\"1st assignment\"}".equals(workAssignment.getPayload()))
            .verifyComplete();

    }

}
