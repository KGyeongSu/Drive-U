package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "laws_file")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LawsFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "law_id")
    private LawsBoard lawsBoard;

    public void setLawsId(LawsBoard lawsBoard) {
        this.lawsBoard = lawsBoard;
    }
}