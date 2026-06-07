package com.zerock.driveu.service.admin;

import com.zerock.driveu.domain.Member;
import com.zerock.driveu.domain.VideoCourse;
import com.zerock.driveu.dto.admin.LVideoCreateDTO;
import com.zerock.driveu.repository.MemberRepository;
import com.zerock.driveu.repository.VideoCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminLVideoService {
    private final VideoCourseRepository videoCourseRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long create(LVideoCreateDTO dto, Long adminSeq) {
        Member admin = memberRepository.findBySeqAndRole(adminSeq, Member.Role.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("관리자 권한이 없습니다."));

        Integer maxCourseOrder = videoCourseRepository.findMaxCourseOrderByCourseType("LVIDEO");
        int nextCourseOrder = maxCourseOrder + 1;

        String embedUrl = convertToEmbedUrl(dto.getVideoUrl());
        String thumbnailUrl = createThumbnailUrl(embedUrl);

        VideoCourse videoCourse = VideoCourse.builder()
                .courseType("LVIDEO")
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .videoUrl(embedUrl)
                .thumbnailUrl(thumbnailUrl)
                .durationSec(0)
                .requiredYn("Y")
                .useYn(dto.getUseYn())
                .createdBy(admin)
                .build();

        return videoCourseRepository.save(videoCourse).getCourseId();
    }

    @Transactional(readOnly = true)
    public Page<VideoCourse> getLearningVideos(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "courseOrder")
        );

        return switch(sort){
            case "lastest" -> videoCourseRepository.findByCourseTypeOrderByCreatedAtDesc(
                    "LVIDEO", pageable
            );
            case "oldest" -> videoCourseRepository.findByCourseTypeOrderByCreatedAtAsc(
                    "LVIDEO", pageable
            );
            default -> videoCourseRepository.findByCourseTypeOrderByCourseOrderAsc(
                    "LVIDEO", pageable
            );
        };

        //return videoCourseRepository.findByCourseTypeOrderByCourseOrderAsc("LVIDEO", pageable);
    }

    private String convertToEmbedUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("YouTube URL을 입력해 주세요.");
        }

        String trimmedUrl = url.trim();

        // 이미 embed URL이면 그대로 사용
        if (trimmedUrl.contains("youtube.com/embed/")) {
            return trimmedUrl.split("\\?")[0];
        }

        // https://youtu.be/영상ID
        if (trimmedUrl.contains("youtu.be/")) {
            String videoId = trimmedUrl
                    .substring(trimmedUrl.indexOf("youtu.be/") + 9)
                    .split("[?&]")[0];

            return "https://www.youtube.com/embed/" + videoId;
        }

        // https://www.youtube.com/watch?v=영상ID
        if (trimmedUrl.contains("youtube.com/watch")) {
            int vIndex = trimmedUrl.indexOf("v=");

            if (vIndex < 0) {
                throw new IllegalArgumentException("YouTube 영상 ID를 찾을 수 없습니다.");
            }

            String videoId = trimmedUrl
                    .substring(vIndex + 2)
                    .split("[?&]")[0];

            return "https://www.youtube.com/embed/" + videoId;
        }

        // shorts URL 대응
        if (trimmedUrl.contains("youtube.com/shorts/")) {
            String videoId = trimmedUrl
                    .substring(trimmedUrl.indexOf("/shorts/") + 8)
                    .split("[?&]")[0];

            return "https://www.youtube.com/embed/" + videoId;
        }

        throw new IllegalArgumentException("지원하지 않는 YouTube URL 형식입니다.");
    }

    private String createThumbnailUrl(String url) {

        String embedUrl = convertToEmbedUrl(url);
        String videoId = embedUrl.substring(embedUrl.lastIndexOf("/") + 1)
                .split("[?&]")[0];
        ;

        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    @Transactional
    public void update(Long courseId, String useYn) {
        if (!"Y".equals(useYn) && !"N".equals(useYn)) {
            throw new IllegalArgumentException("사용 여부 값이 올바르지 않습니다.");
        }

        VideoCourse videoCourse = videoCourseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("학습 영상을 찾을 수 없습니다."));

        if (!"LVIDEO".equals(videoCourse.getCourseType())) {
            throw new IllegalArgumentException("학습 영상 데이터가 아닙니다.");
        }

        videoCourse.setUseYn(useYn);
    }

    @Transactional
    public void delete(Long courseId) {
        VideoCourse videoCourse = videoCourseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 학습 영상이 없습니다."));

        if (!"LVideo".equals(videoCourse.getCourseType())) {
            throw new IllegalArgumentException("학습 영상 데이터가 아닙니다.");
        }

        videoCourseRepository.delete(videoCourse);
    }

}



































