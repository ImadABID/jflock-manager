package fr.abied.jflock.manager;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;

@Getter
class CoordinatorByWorkType {
    
    private WorkDescription<?, ?> workDescription;
    private LinkedBlockingQueue<Worker> workerQueue;

    CoordinatorByWorkType(WorkDescription<?, ?> workDescription){
        this.workDescription = workDescription;
        this.workerQueue = new LinkedBlockingQueue<>();
    }

}
