package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardMetaDTO {

    private long totalUser;
    private long increaseUser;
    private long eduPass;
    private float eduPassRate;
    private long testApply;
    private long passCount;

}
