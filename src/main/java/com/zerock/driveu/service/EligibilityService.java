package com.zerock.driveu.service;

import com.zerock.driveu.constant.CourseType;
import com.zerock.driveu.repository.ChapterProgressRepository;
import com.zerock.driveu.repository.VideoChapterRepository;
import com.zerock.driveu.repository.VideoProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final VideoProgressRepository videoProgressRepository;

    public boolean hasWrittenExamEligibility(Long userSeq, String memberType) {
        return videoProgressRepository
                .existsByUserSeqAndMemberTypeAndCourse_CourseTypeAndFinalCompletedYn(
                        userSeq,
                        memberType,
                        CourseType.DU,
                        "Y"
                );
    }

    public boolean canUseCbt(Long userSeq, String memberType) {
        return hasWrittenExamEligibility(userSeq, memberType);
    }
}