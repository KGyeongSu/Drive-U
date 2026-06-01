package com.zerock.driveu.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterMenuDTO {

    private Long chapterId;
    private Integer chapterOrder;
    private String chapterTitle;

    private boolean completed;
    private boolean accessible;
}