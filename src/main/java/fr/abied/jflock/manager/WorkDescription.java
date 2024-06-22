
package fr.abied.jflock.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A work assignment
 * S work submission
 * 
 * Both A and S must have a "no arg constructor"
 * TODO add an assert for that
 */
@AllArgsConstructor
@Getter
@Slf4j
public abstract class WorkDescription<A, S> {

    // Due to type erasure, it is important to give work assignment/subscription class at the constructor.
    private Class<A> workAssignmentType;
    private Class<S> workSubmissionType;

    public abstract String getPackageName();

    public abstract void workSubmissionHandler(S workSubmission);

    public abstract Flux<A> getWorkAssignmentFlux();

    public void workSubmissionHandlerSerializationProxy(WorkSubmission workSubmission){
        try {
            ObjectMapper jackson = new ObjectMapper();
            S workSubmissionPayload = jackson.readValue(workSubmission.getPayload(), workSubmissionType);
            this.workSubmissionHandler(workSubmissionPayload);
        } catch (Exception e) {
            /* TODO
             * In this case, log the error and stop communicating with the worker
             * Think of a way to retry processing the assignment by another worker
             */
            // 
            throw new WorkManagerRuntimeException(String.format("Cannot deserialize the work submission payload: %s", e.getMessage()));
        }
    }

    public Flux<WorkAssignment> getWorkAssignmentFluxProxy(){
        ObjectMapper jackson = new ObjectMapper();
        return getWorkAssignmentFlux().flatMap(workAssignmentUserObj->{
            try {
                return Mono.just(new WorkAssignment(getPackageName(), jackson.writeValueAsString(workAssignmentUserObj)));
            } catch (JsonProcessingException e) {
                /*
                 * We fixing the bug an restarting the app, the assignment that were replaced by empty
                 * are going to be re-processed
                 */
                log.error(e.getMessage());
                return Mono.empty();
            }
        });
    }

}
