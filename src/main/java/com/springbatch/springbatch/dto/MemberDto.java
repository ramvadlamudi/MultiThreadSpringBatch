package com.springbatch.springbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String team;
    private String status;


}
