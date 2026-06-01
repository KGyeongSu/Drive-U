package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileDTO {

    private Long id;
    private String fileName;

    // requestDTO : filePath, responseDTO : fileUrl 역할
    private String fileUrl;

}
