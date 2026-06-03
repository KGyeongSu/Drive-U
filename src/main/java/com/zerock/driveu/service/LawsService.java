package com.zerock.driveu.service;

import com.zerock.driveu.domain.LawsBoard;
import com.zerock.driveu.domain.LawsFile;
import com.zerock.driveu.dto.*;
import com.zerock.driveu.repository.LawsRepository;
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
public class LawsService {

    private final LawsRepository lawsRepository;
    private final FileUtils fileUtils;

    // 리스트 가져오기
    public Page<LawsListDTO> getList(Pageable pageable) {
        return lawsRepository.findAll(pageable)
                .map(l -> LawsListDTO.builder()
                        .id(l.getId())
                        .title(l.getLaw_title())
                        .regDate(l.getLaw_regDate())
                        .modDate(l.getLaw_modDate())
                        .build());
    }

    // detail 가져오기
    public LawsResponseDTO getOne(Long id) {
        LawsBoard l = lawsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 법규는 존재하지 않습니다."));

        List<LawsFileDTO> fileDTO = l.getFileList().stream()
                .map(file -> LawsFileDTO.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .fileUrl("/download/laws/" + file.getId())
                        .build())
                .collect(Collectors.toList());

        return LawsResponseDTO.builder()
                .id(l.getId())
                .title(l.getLaw_title())
                .content(l.getLaw_content())
                .files(fileDTO)
                .regDate(l.getLaw_regDate())
                .modDate(l.getLaw_modDate())
                .build();
    }

    // 법규 등록
    @Transactional
    public void registerLaws(LawsRequestDTO lawsRequestDTO, List<MultipartFile> files) throws IOException {
        LawsBoard laws = LawsBoard.builder()
                .law_title(lawsRequestDTO.getTitle())
                .law_content(lawsRequestDTO.getContent())
                .build();

        // 파일 유효성 검사 -> 빈 파일 리스트 검증
        List <MultipartFile> validFiles = checkValidFile(files);

        // 업로드 진행
        if (!validFiles.isEmpty()) {

            List<UploadFileDTO> result = fileUtils.uploadFiles(validFiles, "laws");

            result.forEach(r -> {
                laws.addFile(LawsFile.builder()
                        .fileName(r.getFileName())
                        .filePath(r.getFilePath())
                        .uuid(r.getUuid())
                        .build());
            });
        }
        lawsRepository.save(laws);
    }

    // 법규 수정
    @Transactional
    public void updateLaws(Long id, LawsRequestDTO lawsRequestDTO, List<MultipartFile> newFiles, List<Long> deleteIds) throws IOException {
        LawsBoard l = lawsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 법규를 찾을 수 없습니다."));

        if (deleteIds != null && !deleteIds.isEmpty()) {
            List<LawsFile> removeFile = l.getFileList().stream()
                    .filter(f -> deleteIds.contains(f.getId()))
                    .collect(Collectors.toList());

            List <String> removeFilePath = removeFile.stream()
                    .map(LawsFile :: getFilePath).collect(Collectors.toList());

            fileUtils.deleteFile(removeFilePath);

            l.getFileList().removeAll(removeFile);

        }

        // 파일 유효성 검사 -> 빈 파일 리스트 검증
        List <MultipartFile> validFiles = checkValidFile(newFiles);

        if (!validFiles.isEmpty()) {

            List<UploadFileDTO> result = fileUtils.uploadFiles(validFiles, "law");

            result.forEach(r -> {

                l.addFile(LawsFile.builder()
                        .fileName(r.getFileName())
                        .filePath(r.getFilePath())
                        .uuid(r.getUuid())
                        .build());

            });

        }

        l.updateTextOnly(lawsRequestDTO.getTitle(), lawsRequestDTO.getContent());

    }

    // 법규 삭제
    @Transactional
    public void deleteLaws(Long id) {
        LawsBoard laws = lawsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 법규를 찾을 수 없습니다."));

        List<String> target = laws.getFileList().stream()
                .map(LawsFile::getFilePath)
                .collect(Collectors.toList());

        lawsRepository.delete(laws);
        fileUtils.deleteFile(target);
    }

    private List <MultipartFile> checkValidFile (List<MultipartFile> files) {

        if (files == null) {

            return new ArrayList<>();

        }

        return files.stream()
                .filter(f -> f != null && !f.isEmpty() && f.getOriginalFilename() != null && !f.getOriginalFilename().isEmpty())
                .collect(Collectors.toList());

    }

}

