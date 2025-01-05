package com.springbatch.springbatch.dto;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class FailedRecordTracker {
    private Set<MemberDto> failedRecords = new HashSet<>();

    public void addFailedRecord(MemberDto record) {
        failedRecords.add(record);  // Adding to a set automatically handles duplicates
    }

    public Set<MemberDto> getFailedRecords() {
        return failedRecords;
    }

    public void addFailedRecords(Set<MemberDto> memberDtos) {
        failedRecords.addAll(memberDtos);  // Adding a set of records
    }

    public void clearFailedRecords() {
        failedRecords.clear();  // Clear the set of failed records
    }
}
