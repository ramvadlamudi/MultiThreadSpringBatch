package com.springbatch.springbatch.Writer;

import com.springbatch.springbatch.dto.FailedRecordTracker;
import com.springbatch.springbatch.dto.MemberDto;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecordWriter implements ItemWriter<MemberDto> {

    private ExecutorService executorService;

    @Autowired
    private RestApiItemWriter restApiItemWriter;

    @Autowired
    private FailedRecordTracker failedRecordTracker;


    public RecordWriter() {
        this.executorService = Executors.newFixedThreadPool(2);  // Adjust the number of threads
    }

    @Override
    public void write(List<? extends MemberDto> items) throws Exception {
        List<Future<Void>> futures = new ArrayList<>();

        int partitionSize = 100; // Adjust partition size as needed
        List<MemberDto> allRecords = (List<MemberDto>) items; // Assuming 'items' is the list of MemberDto

        for (int i = 0; i < allRecords.size(); i += partitionSize) {
            // Create a sublist (partition) of records
            List<MemberDto> partition = allRecords.subList(i, Math.min(i + partitionSize, allRecords.size()));
            // Create a task for processing this partition and submit it to the executor
            System.out.println("Processing partition size: " + partition.size());
            ProcessRecordsTask task = new ProcessRecordsTask(restApiItemWriter,partition,failedRecordTracker);
            Future<Void> future = executorService.submit(task);
            futures.add(future);
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                future.get(); // This will block until the task is complete
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
