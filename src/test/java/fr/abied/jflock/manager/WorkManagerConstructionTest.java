package fr.abied.jflock.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

import reactor.core.publisher.Flux;

@SpringBootTest
@DirtiesContext
class WorkManagerConstructionTest {

    @TestConfiguration
    static class SpringConfiguration {
    
        @Bean
        WorkDescription<WorkAssignmentExample, WorkSubmissionExample> work1(){
            return new WorkDescriptionExample("say_hello.in_arabic");
        }

        @Bean
        WorkDescription<WorkAssignmentExample, WorkSubmissionExample> work2(){
            return new WorkDescriptionExample("say_hello.in_french");
        }

    }
    
    @Autowired
    private WorkManager workManager;

    @Test
    void testContext(){
        assertNotNull(this.workManager);
        assertEquals(2, workManager.getCoordinators().size());
    }

    @Test
    void testPackagePackageConflict(){

        List<WorkDescription<?, ?>> workDescriptions = new ArrayList<>();
        workDescriptions.add(new WorkDescriptionExample("say_hello"));
        workDescriptions.add(new WorkDescriptionExample("say_hello"));

        assertThrows(WorkManagerRuntimeException.class, ()->{
            new WorkManager(workDescriptions);
        });
    }

}


class WorkDescriptionExample extends WorkDescription<WorkAssignmentExample, WorkSubmissionExample>{

    private String packageName;

    public WorkDescriptionExample(String packageName) {
        super(WorkAssignmentExample.class, WorkSubmissionExample.class);
        this.packageName = packageName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void workSubmissionHandler(WorkSubmissionExample workSubmission) {
        // Not necessary for this test
    }

    @Override
    public Flux<WorkAssignmentExample> getWorkAssignmentFlux() {
        return Flux.empty();
    }

}