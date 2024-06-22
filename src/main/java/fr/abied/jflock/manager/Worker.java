package fr.abied.jflock.manager;

import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

class Worker{

    private CoordinatorByWorkType coordinatorByWorkType;
    private Flux<WorkSubmission> workSubmissionFlux;
    private Sinks.Many<WorkAssignment> workAssignmentSink;
    private boolean busy;

    public Worker(CoordinatorByWorkType coordinatorByWorkType, Flux<WorkSubmission> workSubmissionFlux){
        this.coordinatorByWorkType = coordinatorByWorkType;
        this.workSubmissionFlux = workSubmissionFlux;
        this.workAssignmentSink = Sinks.many().unicast().onBackpressureBuffer();
        this.busy = false;

        try {
            this.coordinatorByWorkType.getWorkerQueue().put(this);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Flux<WorkAssignment> getWorkAssignmentFlux(){
        return workAssignmentSink.asFlux();
    }

    /**
     * It is responsible for handling work submissions.
     * Once the work submission is handled, it returns this worker to the poll.
     */
    public void run(){

        Consumer<WorkSubmission> workSubmissionHandler = coordinatorByWorkType.getWorkDescription()::workSubmissionHandlerSerializationProxy;

        // TODO Handel the case when the connection with client is closed unexpectedly

        workSubmissionFlux
                    .doOnNext(workSubmissionHandler
                            .andThen(workSubmission->{
                                try {
                                    busy = false;
                                    this.coordinatorByWorkType.getWorkerQueue().put(this);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }    
                            }))
                    .blockLast();
    }

    public void assignWork(WorkAssignment workAssignment){
        if(busy){
            throw new WorkerBusy();
        }
        busy = true;
        while(workAssignmentSink.tryEmitNext(workAssignment) != EmitResult.OK);
    }

    // TODO implement shutdown gracefully

}
