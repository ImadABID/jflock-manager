package fr.abied.jflock.manager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class WorkerTest {
    
    @Test
    void testWorkAssignment() throws InterruptedException{

        class WorkDescriptionExample extends WorkDescription<String, String>{

            public WorkDescriptionExample() {
                super(String.class, String.class);
            }
        
            @Override
            public String getPackageName() {
                return "say_hello";
            }

            @Override
            public void workSubmissionHandler(String workSubmission) {
                // Not necessary for this test
            }

            @Override
            public Flux<String> getWorkAssignmentFlux() {
                return Flux.empty();
            }
        
        }

        WorkDescription<String, String> work = new WorkDescriptionExample();

        // At WorkManager initialization
        CoordinatorByWorkType coordinatorByWorkType = new CoordinatorByWorkType(work);
        assertEquals(0, coordinatorByWorkType.getWorkerQueue().size());

        // At the Controller
        Worker workerFromTheControllerPointOfView = new Worker(coordinatorByWorkType, Flux.empty());
        Flux<WorkAssignment> assignmentFlux = workerFromTheControllerPointOfView.getWorkAssignmentFlux();
        assertEquals(1, coordinatorByWorkType.getWorkerQueue().size());

        // At WorkManager, when it want to assign a work
        Worker workerFromTheWorkManagerPointOfView = coordinatorByWorkType.getWorkerQueue().take();
        assertEquals(0, coordinatorByWorkType.getWorkerQueue().size());
        assertDoesNotThrow(()->
            workerFromTheWorkManagerPointOfView.assignWork(new WorkAssignment("say_hello", "Alex")));

        WorkAssignment workAssignment = new WorkAssignment("say_hello", "Bob");
        assertThrows(WorkerBusy.class, ()->
            workerFromTheWorkManagerPointOfView.assignWork(workAssignment));
        
        // At the underlying Object (ex: RSocket) responsible for sending the work assignment to the worker.
        StepVerifier.create(assignmentFlux.take(1))
                .expectNextMatches(wa->"Alex".equals(wa.getPayload()))
                .verifyComplete();
    
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void testWorkSubmission(){

        WorkDescription<String, String> work = mock(WorkDescription.class);

        when(work.getPackageName()).thenReturn("say_hello");
        when(work.getWorkAssignmentFlux()).thenReturn(Flux.empty());

        // At WorkManager initialization
        CoordinatorByWorkType coordinatorByWorkType = new CoordinatorByWorkType(work);

        // At the Controller
        Worker workerFromTheControllerPointOfView = new Worker(
            coordinatorByWorkType,
            Flux.just(
                new WorkSubmission("say_hello", "Hello Alex", null),
                new WorkSubmission("say_hello", "Hello Bob", null)));

        workerFromTheControllerPointOfView.run();

        verify(work, times(2)).workSubmissionHandlerSerializationProxy(any(WorkSubmission.class));
    
    }

    @Test
    void testWorkerBusy(){

        @SuppressWarnings("unchecked")
        WorkDescription<String, String> work = mock(WorkDescription.class);

        // At WorkManager initialization
        CoordinatorByWorkType coordinatorByWorkType = new CoordinatorByWorkType(work);

        // At the Controller
        Worker worker = new Worker(coordinatorByWorkType, Flux.empty());
        worker.assignWork(new WorkAssignment("say_hello", "Alex"));

        boolean assertThrows = false;
        try{
            worker.assignWork(new WorkAssignment("say_hello", "Alex"));
        }catch(WorkerBusy e){
            assertThrows = true;
            assertNotNull(e.getMessage());
        }
        assertTrue(assertThrows);
    
    }

}
