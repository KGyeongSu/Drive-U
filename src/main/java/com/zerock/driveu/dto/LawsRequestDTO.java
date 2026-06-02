package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawsRequestDTO { //요청시 사용하는 DTO

    private String title; // 엔티티의 law_title로 변환되어 저장됨
    private String content; // 엔티티의 law_content로 변환되어 저장됨

}