package com.springbatch.springbatch.tasklet;

import com.springbatch.springbatch.dto.FailedRecordTracker;
import com.springbatch.springbatch.dto.MemberDto;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class FailedAlertTasklet  implements Tasklet {

    private final FailedRecordTracker failedRecordTracker;

    @Autowired
    public FailedAlertTasklet(FailedRecordTracker failedRecordTracker) {
        this.failedRecordTracker = failedRecordTracker;
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        // Retrieve the failed records
        Set<MemberDto> failedRecords =  failedRecordTracker.getFailedRecords();

        // Check if there are any failed records
        if (failedRecords.isEmpty()) {
            System.out.println("No failed records to process.");
        } else {
            System.err.println("Found " + failedRecords.size() + " failed records.");
            // Log or alert about the failed records
            for (MemberDto record : failedRecords) {
                System.err.println("Failed record: " + record.getId()+"Firts Name "+record.getFirstName());
            }
            System.err.println("Batch job failed due to the presence of failed records.");

            // You can set a flag in the StepContribution to indicate the failure
            stepContribution.setExitStatus(ExitStatus.FAILED);

            // Optionally, you can also throw an exception to fail the job explicitly
            throw new Exception("Batch job failed due to failed records.");

        }

        // Clear the list of failed records after processing
        failedRecordTracker.clearFailedRecords();

        return RepeatStatus.FINISHED;
    }
}
