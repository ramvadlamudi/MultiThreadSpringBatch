package com.springbatch.springbatch.Writer;

import com.springbatch.springbatch.dto.FailedRecordTracker;
import com.springbatch.springbatch.dto.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@Component
public class ProcessRecordsTask implements Callable<Void> {


    private FailedRecordTracker failedRecordTracker;
    private  RestApiItemWriter restApiItemWriter;
    private final List<MemberDto> records;
    private final List<MemberDto> failedRecords = new ArrayList<>();  // Track failed records

    @Autowired
    public ProcessRecordsTask(RestApiItemWriter restApiItemWriter,List<MemberDto> records,FailedRecordTracker failedRecordTracker) {
        this.restApiItemWriter = restApiItemWriter;
        this.records = records;
        this.failedRecordTracker = failedRecordTracker;
    }

    @Override
    public Void call() throws Exception {
        for (MemberDto record : records) {
            try {
                restApiItemWriter.sendMemberRequest(record);
            } catch (HttpClientErrorException e) {
                // Handle client error (4xx)
                System.err.println("Client error while processing record: " + record);
                e.printStackTrace();
                failedRecords.add(record);
            } catch (HttpServerErrorException e) {
                // Handle server error (5xx)
                System.err.println("Server error while processing record: " + record);
                e.printStackTrace();
                failedRecords.add(record);
            } catch (IOException e) {
                // Handle network issues
                System.err.println("Network issue while processing record: " + record);
                e.printStackTrace();
                failedRecords.add(record);
            } catch (InterruptedException e) {
                // Handle thread interruption during retry delay
                System.err.println("Thread interrupted while retrying: " + record);
                e.printStackTrace();
                failedRecords.add(record);
            } catch (Exception e) {
                // Handle other unexpected exceptions
                System.err.println("Unexpected error while processing record: " + record);
                e.printStackTrace();
                failedRecords.add(record);
            }
        }
        // Optionally, handle retrying or logging the failed records here
        if (!failedRecords.isEmpty()) {
            System.out.println("Total failed records: " + failedRecords.size());
            // You can log these records, send alert, or retry them.
            // For example, using a FailedRecordTracker to persist the failed records
            failedRecordTracker.addFailedRecords((Set<MemberDto>) failedRecords);
        }
        return null;
    }

    // Getter for failed records (optional)
    public List<MemberDto> getFailedRecords() {
        return failedRecords;
    }
}
