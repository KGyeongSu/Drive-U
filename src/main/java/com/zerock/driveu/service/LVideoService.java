package com.zerock.driveu.service;

import com.zerock.driveu.constant.CourseType;
import com.zerock.driveu.domain.VideoCourse;
import com.zerock.driveu.repository.VideoCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public Page<VideoCourse> getLearningVideos(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(
                page,
                size
        );

        return switch (sort) {
            case "lastest" -> videoCourseRepository.findByCourseTypeAndUseYnOrderByCourseOrderAsc(
                    "LVIDEO", "Y", pageable
            );
            case "oldest" -> videoCourseRepository.findByCourseTypeAndUseYnOrderByCreatedAtDesc(
                    "LVIDEO", "Y", pageable
            );
            default -> videoCourseRepository.findByCourseTypeAndUseYnOrderByCreatedAtAsc(
                    "LVIDEO", "Y", pageable
            );
        };

        //return videoCourseRepository.findByCourseTypeOrderByCourseOrderAsc("LVIDEO", pageable);
    }
}