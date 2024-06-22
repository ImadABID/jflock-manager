package fr.abied.jflock.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class WorkAssignmentAndSubmissionTest {

    @Test
    void testWorkAssignment(){
        WorkAssignment workAssignment = new WorkAssignment("say_hello", "say hello");
        assertEquals("say_hello", workAssignment.getPackageName());
        assertEquals("say hello", workAssignment.getPayload());
        assertNotNull(workAssignment.getCreationDate());
    }

    @Test
    void testWorkSubmission(){
        WorkSubmission workSubmission = new WorkSubmission(
            "say_hello", "hello",
            new WorkAssignment("", ""));
        assertEquals("say_hello", workSubmission.getPackageName());
        assertEquals("hello", workSubmission.getPayload());
        assertNotNull(workSubmission.getCreationDate());
        assertNotNull(workSubmission.getWorkAssignment());
    }

}
