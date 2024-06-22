package fr.abied.jflock.manager;

import java.util.Date;

import lombok.Getter;

/**
 * This class was defined to be used in the RSocket Controller.
 * Its wrap the user defined payload to add protocol specific attributes.
 */
@Getter
public class WorkAssignment {
    
    private String packageName;
    private Date creationDate;
    private String payload;

    WorkAssignment(String packageName, String payload){
        this.packageName = packageName;
        this.payload = payload;
        this.creationDate = new Date();
    }

}
