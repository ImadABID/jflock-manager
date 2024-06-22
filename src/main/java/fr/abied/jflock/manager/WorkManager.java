package fr.abied.jflock.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Controller
@Slf4j
public class WorkManager  {

    // The key represents the worker package name as a String.
    private Map<String, CoordinatorByWorkType> coordinatorsByWorkType;

    public WorkManager(List<WorkDescription<?, ?>> workDescriptions){

        coordinatorsByWorkType = new HashMap<>();
        workDescriptions.forEach(workDescription -> {

            assert(workDescription.getPackageName()!=null);
            
            if(coordinatorsByWorkType.containsKey(workDescription.getPackageName())){
                throw new WorkManagerRuntimeException("Found a two work descriptions with the same package name. Package name must be unique for each work description.");
            }

            coordinatorsByWorkType.put(
                workDescription.getPackageName(),
                new CoordinatorByWorkType(workDescription));
            
        });

    }

    @MessageMapping("hiring")
    /**
     * The first work submission sent by the client is only used to know the package name of the worker.
     * @param workSubmissionFlux
     * @return
     */
    Flux<WorkAssignment> hiring(final Flux<WorkSubmission> workSubmissionFlux){

        WorkSubmission initialWorkSubmission = workSubmissionFlux.blockFirst();

        if(initialWorkSubmission == null){
            return Flux.error(new WorkManagerBadRequestRuntimeException("Cannot retrieve the package name from the first work submission."));
        }

        
        CoordinatorByWorkType coordinator =
            coordinatorsByWorkType.get(initialWorkSubmission.getPackageName());
        if (coordinator == null){
            return Flux.error(new WorkManagerBadRequestRuntimeException("The worker is unrecognizable by the work manager."));
        }
        
        log.info("A new worker was connected package name: {}", coordinator.getWorkDescription().getPackageName());

        Flux<WorkSubmission> actualWorkSubmissionFlux = workSubmissionFlux
                .skip(1);
        /* FIXME
         * The fact that the worker add itself in the coordinator worker queue is not clear
         */
        Worker worker = new Worker(coordinator, actualWorkSubmissionFlux);
        worker.run();

        return worker.getWorkAssignmentFlux();

    }

    public Collection<CoordinatorByWorkType> getCoordinators(){
        return coordinatorsByWorkType.values();
    }    

}
