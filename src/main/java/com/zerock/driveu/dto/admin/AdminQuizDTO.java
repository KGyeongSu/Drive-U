package com.zerock.driveu.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminQuizDTO {

    private String questionText;

    private String explanation;

    private Integer quizOrder;

    private Integer correctChoiceOrder;

    private List<String> choices = new ArrayList<>();
}