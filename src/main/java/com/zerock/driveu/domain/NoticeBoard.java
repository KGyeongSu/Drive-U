package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notice")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NoticeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", length = 3000, nullable = false)
    private String content;

    // 첨부파일
    // mappedBy : 1:N 관계시 종속되는 테이블의 멤버 필드명(FK)을 통해 관리됨
    // cascade : 게시글 삭제 시 파일 테이블의 정보도 삭제
    // orphan : 게시글 삭제 X, 자식과 관계 끊을 때 파일 테이블 정보도 삭제 (게시글 수정과 동시에 파일 수정시 등)
    @OneToMany(mappedBy = "noticeBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoticeFile> fileList = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime regDate;

    @UpdateTimestamp
    private LocalDateTime modDate;

    // 영속성 관리
    // 공지사항 등록 시 파일 존재하면 noticeBoard entity 객체에 저장
    // 생성된 noticeBoard id 를 noticeFiles 에다가 저장
    public void addFile (NoticeFile file) {

        this.fileList.add(file);

        file.setNoticeId(this);

    }

    // 공지사항 수정 method
    public void updateNotice (String title, String content, List <NoticeFile> files) {

        this.title = title;
        this.content = content;

        this.fileList.clear();

        for (NoticeFile file : files) {

            addFile(file);

        }

    }

    // 파일은 수정 안 됐을 경우
    public void updateTextOnly (String title, String content) {

        this.title = title;
        this.content = content;

    }

}
