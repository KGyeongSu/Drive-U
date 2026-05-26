package com.zerock.driveu.service;

import com.zerock.driveu.constant.CourseType;
import com.zerock.driveu.domain.VideoCourse;
import com.zerock.driveu.repository.VideoCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LVideoService {

    private final VideoCourseRepository videoCourseRepository;

    // 학습영상 목록 조회
    public List<VideoCourse> getLVideoList() {
        return videoCourseRepository
                .findByCourseTypeAndUseYnOrderByCourseOrderAsc(
                        CourseType.LVIDEO,
                        "Y"
                );
    }

    // 학습영상 상세 조회
    public Optional<VideoCourse> getLVideo(Long courseId) {
        return videoCourseRepository
                .findByCourseIdAndCourseTypeAndUseYn(
                        courseId,
                        CourseType.LVIDEO,
                        "Y"
                );
    }
}