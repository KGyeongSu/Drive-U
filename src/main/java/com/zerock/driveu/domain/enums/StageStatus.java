package com.zerock.driveu.domain.enums;

public enum StageStatus {
    DONE,     // 이미 통과/이수한 단계
    CURRENT,  // 지금 진행할 차례인 단계 (직전까지 다 했고 이건 아직)
    LOCKED    // 선행조건 미충족으로 잠긴 단계
}