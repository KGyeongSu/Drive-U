package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "laws")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LawsBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "law_id")
    private Long id;

    @Column(name = "law_title", nullable = false)
    private String law_title;

    @Column(name = "law_content", length = 3000, nullable = false)
    private String law_content;

    @OneToMany(mappedBy = "lawsBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LawsFile> fileList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "law_reg_date")
    private LocalDateTime law_regDate;

    @UpdateTimestamp
    @Column(name = "law_mod_date")
    private LocalDateTime law_modDate;

    // 편의 메서드도 필드명에 맞춰 수정
    public void addFile(LawsFile file) {
        this.fileList.add(file);
        file.setLawsId(this);
    }

    public void updateLaws(String law_title, String law_content, List<LawsFile> files) {
        this.law_title = law_title;
        this.law_content = law_content;
        this.fileList.clear();
        for (LawsFile file : files) {
            addFile(file);
        }
    }

    public void updateTextOnly(String law_title, String law_content) {
        this.law_title = law_title;
        this.law_content = law_content;
    }
}