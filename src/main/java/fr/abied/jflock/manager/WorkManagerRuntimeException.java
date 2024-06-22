package fr.abied.jflock.manager;

/**
 * Thrown when the Worker Manager is not used correctly by the server
 */
public class WorkManagerRuntimeException extends RuntimeException {
    public WorkManagerRuntimeException(String error){
        super(error);
    }
}
