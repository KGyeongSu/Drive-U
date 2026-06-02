package com.zerock.driveu.service;

import com.zerock.driveu.dto.NoticeRequestDTO;
import com.zerock.driveu.dto.NoticeResponseDTO;
import com.zerock.driveu.repository.NoticeFileRepository;
import com.zerock.driveu.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeFileRepository noticeFileRepository;

    @Test
    public void registerNotice () throws IOException {

        for (int i = 0; i <21; i++) {

            List<MultipartFile> files = new ArrayList<>();

            if (i % 2 != 0) {

                MockMultipartFile file = new MockMultipartFile(
                        "files", "test.pdf", "application/pdf", "content".getBytes());
                files.add(file);

            }

            NoticeRequestDTO noticeRequestDTO = NoticeRequestDTO.builder()
                    .title("공지사항" + i)
                    .content("공지사항 내용" + i)
                    .build();

            noticeService.registerNotice(noticeRequestDTO, files);

        }

        assertThat(noticeRepository.count()).isEqualTo(21);
        assertThat(noticeFileRepository.count()).isEqualTo(10);

    }

    @Test
    public void updateNotice () throws IOException {

        Long id = 20L;
        List<Long> keepFiles = Arrays.asList(3L);
        List<MultipartFile> newFiles = new ArrayList<>();

        List <MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("files", "수정된파일.pdf", "application/pdf", "data".getBytes()));

        NoticeRequestDTO updateDTO = NoticeRequestDTO.builder()
                .title("수정된 공지사항 제목")
                .content("수정된 공지사항 컨텐츠 임돠")
                .build();

        noticeService.updateNotice(id, updateDTO, newFiles, keepFiles);

        NoticeResponseDTO result = noticeService.getOne(id);

        assertThat(result.getTitle()).isEqualTo("수정된 공지사항 제목");
        assertThat(result.getContent()).isEqualTo("수정된 공지사항 컨텐츠 임돠");
        assertThat(result.getFiles().size()).isEqualTo(1);
        assertThat(result.getFiles().get(0).getFileName()).isEqualTo("수정된_첨부파일.pdf");

    }

    @Test
    public void deleteNotice() {

        Long id = 20L;

        noticeService.deleteNotice(id);

        assertThat(noticeRepository.count()).isEqualTo(20);
        assertThat(noticeFileRepository.count()).isEqualTo(9);

    }



}