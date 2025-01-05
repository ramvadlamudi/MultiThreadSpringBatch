package com.springbatch.springbatch.config;

import com.springbatch.springbatch.Writer.RecordWriter;
import com.springbatch.springbatch.dto.MemberDto;
import com.springbatch.springbatch.processor.MemberItemProcessor;
import com.springbatch.springbatch.reader.FileRecordReader;
import com.springbatch.springbatch.tasklet.FailedAlertTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private FailedAlertTasklet failedAlertTasklet;

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("processJob")
                .start(processStep())
                .next(processFailedRecordsStep())
                .build();
    }
    @Primary
    @Bean
    public Step processStep() {
        return stepBuilderFactory.get("processStep")
                .<MemberDto, MemberDto>chunk(1000)  // Process records in chunks of 1000
                .reader(recordReader())
                .processor(recordProcessor())
                .writer(recordWriter())
                .build();
    }

    @Bean
    public ItemReader<MemberDto> recordReader() {
        return new FileRecordReader(); // Implement this reader to read records from a file
    }

    @Bean
    public ItemProcessor<MemberDto, MemberDto> recordProcessor() {
        return new MemberItemProcessor(); // Optionally process the records
    }

    @Bean
    public ItemWriter<MemberDto> recordWriter() {
        return new RecordWriter(); // Implement this writer to send records to the third-party URL
    }

    @Bean
    public Step processFailedRecordsStep() {
        return stepBuilderFactory.get("processFailedRecordsStep")
                .tasklet(failedAlertTasklet)
                .build();
    }


}
