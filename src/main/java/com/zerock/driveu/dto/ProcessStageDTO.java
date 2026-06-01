package com.zerock.driveu.dto;

import com.zerock.driveu.domain.enums.StageStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStageDTO {

    private String key;          // 단계 식별 키 ("DU","WRITTEN","FUNCTION"...) — 화면 분기/디버깅용
    private String title;        // 화면에 보일 단계명
    private StageStatus status;  // DONE / CURRENT / LOCKED
    private String linkUrl;      // 이 단계에서 이동할 URL
}