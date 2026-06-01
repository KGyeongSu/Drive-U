package com.zerock.driveu.service;

import com.zerock.driveu.domain.NoticeBoard;
import com.zerock.driveu.domain.NoticeFile;
import com.zerock.driveu.dto.*;
import com.zerock.driveu.repository.NoticeRepository;
import com.zerock.driveu.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileUtils fileUtils;

    // 리스트 가져오기
    public Page<NoticeListDTO> getList (Pageable pageable) {

        return noticeRepository.findAll(pageable)
                .map(n -> NoticeListDTO.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .regDate(n.getRegDate())
                        .modDate(n.getModDate())
                        .build());

    }

    // detail 가져오기
    public NoticeResponseDTO getOne (Long id) {

        NoticeBoard n = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항은 존재하지 않습니다."));

        // notice의 fileList를 noticeFileDTO로 변환해 가져오기
        // collect : file이 여러 개면 묶어주려고 > toList() 로도 가능
        List<NoticeFileDTO> fileDTO = n.getFileList().stream()
                .map(file -> NoticeFileDTO.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .fileUrl("/download/" + file.getId())
                        .build())
                .collect(Collectors.toList());

        return NoticeResponseDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .files(fileDTO)
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .build();

    }

    // 공지사항 등록
    @Transactional
    public void registerNotice (NoticeRequestDTO noticeRequestDTO, List<MultipartFile> files) throws IOException {

        NoticeBoard notice = NoticeBoard.builder()
                .title(noticeRequestDTO.getTitle())
                .content(noticeRequestDTO.getContent())
                .build();

        // 파일 저장 및 정보 가져오기
        if (files != null && !files.isEmpty()) {

            List <UploadFileDTO> result = fileUtils.uploadFiles(files, "notice");

            // dto 정보 entity에 세팅
            result.forEach(r -> {

                System.out.println("디버깅 - 파일명: " + r.getFileName());

                notice.addFile(NoticeFile.builder()
                        .fileName(r.getFileName())
                        .filePath(r.getFilePath())
                        .uuid(r.getUuid())
                        .build());

            });

        }

        noticeRepository.save(notice);

    }

    // 공지사항 수정
    @Transactional
    public void updateNotice (Long id, NoticeRequestDTO noticeRequestDTO, List<MultipartFile> files) throws IOException {

        // 공지사항 존재 여부 확인
        NoticeBoard n = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다."));

        // 새로운 파일 리스트 담을 식별자
        List <NoticeFile> newFile = new ArrayList<>();

        // 파일이 새로 들어온 경우
        if (files != null && !files.isEmpty()) {

            // 기존 파일 경로 전달
            List <String> oldFile = n.getFileList().stream()
                    .map(NoticeFile::getFilePath)
                    .collect(Collectors.toList());

            // 수정하는 경우에는 clear되기 때문에 리스트 새로 필요 - 폴더에 저장되는 곳
            List <UploadFileDTO> result = fileUtils.uploadFiles(files, "notice");

            // 폴더에 저장된 곳을 디비 저장용에다가 정보 넣어줌
            newFile = result.stream()
                    .map(r -> NoticeFile.builder()
                            .fileName(r.getFileName())
                            .filePath(r.getFilePath())
                            .uuid(r.getUuid())
                            .build())
                    .collect(Collectors.toList());

            // 공지사항 수정
            n.updateNotice(noticeRequestDTO.getTitle(), noticeRequestDTO.getContent(), newFile);

            // 수정 성공 시 실제 파일 삭제
            fileUtils.deleteFile(oldFile);

        } else {

            // 글만 수정된 경우
            n.updateTextOnly(noticeRequestDTO.getTitle(), noticeRequestDTO.getContent());

        }

    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice (Long id) {

        NoticeBoard notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다."));

        // 실제파일 경로 추출
        List <String> target = notice.getFileList().stream()
                        .map(NoticeFile::getFilePath)
                        .collect(Collectors.toList());

        noticeRepository.delete(notice);

        // 실제 파일 삭제
        fileUtils.deleteFile(target);

    }

}
