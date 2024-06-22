package fr.abied.jflock.manager;

public class WorkerBusy extends RuntimeException{
    WorkerBusy(){
        super();
    }

    @Override
    public String getMessage(){
        return "Trying to assign a new work to a busy worker. Review how Worker.assignWork is called to avoid this.";
    }
}
