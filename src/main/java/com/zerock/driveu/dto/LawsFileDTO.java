package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawsFileDTO {

    private Long id;
    private String fileName;
    private String fileUrl; // /download/laws/id 와 같은 형태로 사용

}