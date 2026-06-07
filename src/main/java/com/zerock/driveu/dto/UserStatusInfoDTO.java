package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusInfoDTO {

    private String name;
    private String userEmail;
    private String eduStatus;
    private String testStatus;
    private String functionStatus;
    private String driveStatus;
    private LocalDateTime recentDate;

}
