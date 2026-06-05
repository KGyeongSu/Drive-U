package com.zerock.driveu.controller;

import com.zerock.driveu.domain.LawsFile;
import com.zerock.driveu.domain.NoticeFile;
import com.zerock.driveu.repository.LawsFileRepository;
import com.zerock.driveu.repository.NoticeFileRepository;
import com.zerock.driveu.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileUtils fileUtils;
    private final NoticeFileRepository noticeRepository;
    private final LawsFileRepository lawsFileRepository;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile (@PathVariable Long id) throws IOException {

        // DB에서 정보 조회
        NoticeFile noticeFile = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // fileUtils로 폴더에서 파일 리소스 가져오기
        Resource resource = fileUtils.downFile(noticeFile.getFilePath());

        // 한글 깨짐 방지
        String fileName = URLEncoder.encode(noticeFile.getFileName(), StandardCharsets.UTF_8);

        //반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    @GetMapping("/download/laws/{id}")
    public ResponseEntity<Resource> downloadLawsFile (@PathVariable Long id) throws IOException {

        // DB에서 법규 파일 정보 조회
        LawsFile lawsFile = lawsFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("법규 파일을 찾을 수 없습니다."));

        // fileUtils로 폴더에서 파일 리소스 가져오기
        Resource resource = fileUtils.downFile(lawsFile.getFilePath());

        // 한글 깨짐 방지
        String fileName = URLEncoder.encode(lawsFile.getFileName(), StandardCharsets.UTF_8);

        // 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
