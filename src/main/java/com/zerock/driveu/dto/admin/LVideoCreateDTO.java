package com.zerock.driveu.dto.admin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LVideoCreateDTO {

    private String category;

    private String title;

    private String videoUrl;

    private String description;

    private String useYn;
}