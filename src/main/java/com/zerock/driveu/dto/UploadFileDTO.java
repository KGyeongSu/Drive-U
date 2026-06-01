package com.zerock.driveu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadFileDTO {

    private String fileName;
    private String filePath;
    private String uuid;

}
