package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notice_file")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NoticeFile {

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
    // JPA가 해당 class의 id를 가져옴
    @JoinColumn(name = "notice_id")
    private NoticeBoard noticeBoard;

    public void setNoticeId(NoticeBoard noticeBoard) {

        this.noticeBoard = noticeBoard;

    }

}
