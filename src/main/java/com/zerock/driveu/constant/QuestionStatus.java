package com.zerock.driveu.constant;

public enum QuestionStatus {

    // 내부 로직용
    PENDING("답변예정"),
    COMPLETED("답변완료");

    private final String description;

    // pending of completed의 상태값을 전달하기 위함 -> 표출용
    QuestionStatus (String description) {

        this.description = description;

    }

    public String getDescription () {

        return description;

    }

}
