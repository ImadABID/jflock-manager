package fr.abied.jflock.manager;

/**
 * Thrown when the client does not request the server correctly
 */
public class WorkManagerBadRequestRuntimeException extends RuntimeException {
    public WorkManagerBadRequestRuntimeException(String error){
        super(error);
    }
}