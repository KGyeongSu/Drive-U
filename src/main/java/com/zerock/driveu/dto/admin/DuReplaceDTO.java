package com.zerock.driveu.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DuReplaceDTO {

    private String chapterTitle;

    private String videoUrl;

    private Integer startSec;

    private Integer endSec;

    private String changeReason;

    private List<AdminQuizDTO> quizzes = new ArrayList<>();
}
