package fr.abied.jflock.manager;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class WorkDispatcher implements ApplicationRunner{
    
    private WorkManager workManager;

    @Override
    @Async
    public void run(ApplicationArguments args) throws Exception {
        workManager.getCoordinators().forEach(coordinator ->{

            Flux<WorkAssignment> workAssignmentFlux = coordinator.getWorkDescription().getWorkAssignmentFluxProxy();
            workAssignmentFlux
                    .subscribe(workAssignment->{
                        boolean workAssigned = false;
                        while (!workAssigned) {
                            try {
                                Worker worker = coordinator.getWorkerQueue().take();
                                worker.assignWork(workAssignment);
                                workAssigned = true;
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        
                    });
        });
    }

}
