package com.zerock.driveu.util;

import com.zerock.driveu.dto.UploadFileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${file.upload.root-path}")
    private String rootPath;

    @Value("${file.upload.notice}")
    private String noticePath;

    @Value("${file.upload.law}")
    private String lawPath;

    // 파일 저장
    public List<UploadFileDTO> uploadFiles (List <MultipartFile> files, String type) throws IOException {

        String subPath = type.equals("notice") ? noticePath : lawPath;
        String fullPath = rootPath + subPath;

        File folder = new File (fullPath);
        // 폴더 없으면 생성
        if (!folder.exists()) folder.mkdirs();

        List <UploadFileDTO> fileEntities = new ArrayList<>();

        for (MultipartFile file : files) {

            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + file.getOriginalFilename();

            // 실제 파일 저장
            file.transferTo(new File(fullPath + savedFileName));

            // 파일 정보 저장
            fileEntities.add(UploadFileDTO.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(subPath + savedFileName)
                    .uuid(uuid)
                    .build());

        }

        return fileEntities;

    }

    // 파일 수정 및 삭제 시
    public void deleteFile (List <String> filePath) {

        for (String file : filePath) {

            // 실제 물리적 경로 완성시키기
            File target = new File(rootPath + file);

            if(target.exists()) {

                target.delete();

            }

        }

    }

    // 파일 다운 시 가져오기
    public Resource downFile (String filePath) throws IOException {

        // Path : 파일 경로, Paths : 객체 생성
        Path path = Paths.get(filePath);

        return new InputStreamResource(Files.newInputStream(path));

    }

}
