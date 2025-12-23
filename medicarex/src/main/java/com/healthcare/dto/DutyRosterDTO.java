package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DutyRosterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String inDutyTime;
    private String status;
}
