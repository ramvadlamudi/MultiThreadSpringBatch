package com.springbatch.springbatch.reader;

import com.springbatch.springbatch.dto.MemberDto;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileRecordReader implements ItemReader<MemberDto> {

    private BufferedReader reader;
    private String line;

    @Override
    public MemberDto read() throws Exception {
        if (line == null) {
            return null;  // End of file
        }
        // Assuming CSV format like "name,email,age"
        String[] values = line.split(",");  // Split the line into fields
        MemberDto record = new MemberDto();
        record.setId(Long.parseLong(values[0]));
        record.setFirstName(values[1]);// Map fields to the Record constructor
        record.setLastName(values[2]);
        record.setJobTitle(values[3]);
        record.setTeam(values[4]);
        record.setStatus(values[5]);
        line = reader.readLine();  // Read the next line
        return record;


    }

    @PostConstruct
    public void init() throws IOException {
        // Initialize the reader (e.g., from a file)
        ClassPathResource classPathResource = new ClassPathResource("sample_data_1.txt");
        reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()));
        //reader = new BufferedReader(new FileReader("path/to/input/file.txt"));
        // Read and discard the first line (header line)
        reader.readLine(); // Skip the first line

        // Now, start reading the second line onwards
        line = reader.readLine(); // Read the next line
    }

    @PreDestroy
    public void cleanup() throws IOException {
        if (reader != null) {
            reader.close();  // Close the reader
        }
    }
}
