package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDTO {

    private Long id;
    private String title;
    private String content;
    private List<NoticeFileDTO> files;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
