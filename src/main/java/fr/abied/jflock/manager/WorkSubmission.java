package fr.abied.jflock.manager;

import java.util.Date;

import lombok.Getter;

/**
 * User defined work submission classes must implement this class.
 * This class was defined to be used in the RSocket Controller.
 * It is useful for defining protocol specific attributes.
 */
@Getter
public class WorkSubmission {
    
    private String packageName;
    private String payload;
    private Date creationDate;
    private WorkAssignment workAssignment;

    WorkSubmission(String packageName, String payload, WorkAssignment workAssignment){
        this.packageName = packageName;
        this.payload = payload;
        this.workAssignment = workAssignment;
        this.creationDate = new Date();
    }

}
