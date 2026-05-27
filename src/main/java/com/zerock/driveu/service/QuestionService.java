package com.zerock.driveu.service;

import com.zerock.driveu.domain.QuestionBoard;
import com.zerock.driveu.dto.QuestionListDTO;
import com.zerock.driveu.dto.QuestionRequestDTO;
import com.zerock.driveu.dto.QuestionResponseDTO;
import com.zerock.driveu.repository.MemberRepository;
import com.zerock.driveu.repository.QuestionRepository;
import com.zerock.driveu.repository.SocialMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 기본적으로는 읽기만 가능, 변동있는 곳에만 @Transactional 붙이면 됨 > DB 부하 및 데이터 보호 목적
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SocialMemberRepository socialMemberRepository;
    private final MemberRepository memberRepository;

    // 문의 등록 : 사용자
    @Transactional
    public Long register (QuestionRequestDTO questionDTO, String email, boolean isSocial, String name) {

        // 작성자 가져오는 서비스 인터페이스 선언 (검증)
        QuestionWriterService writer;

        // 작성자 가져오기
        if (isSocial) {

            writer = socialMemberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("소셜 회원을 찾을 수 없습니다."));

        } else {

            writer = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("일반 회원을 찾을 수 없습니다."));

        }

        // 문의사항 DB에 insert
        QuestionBoard question = QuestionBoard.builder()
                .title(questionDTO.getTitle())
                .content(questionDTO.getContent())
                .writerEmail(writer.getWriterEmail())
                .writerName(name)
                .build();

        return questionRepository.save(question).getId();

    }

    // 리스트 > paging ( 개수는 controller에서 결정 )
    public Page<QuestionListDTO> getList (Pageable pageable) {

        return questionRepository.findAll(pageable)
                .map(q -> QuestionListDTO.builder()
                        .id(q.getId())
                        .title(q.getTitle())
                        .regDate(q.getRegDate())
                        .status(q.getStatus())
                        .writerName(q.getWriterName())
                        .build());

    }

    // detail
    public QuestionResponseDTO getOne (Long id) {

        QuestionBoard q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의는 존재하지 않습니다."));

        return QuestionResponseDTO.builder()
                .id(q.getId())
                .title(q.getTitle())
                .content(q.getContent())
                .answer(q.getAnswer())
                .writerName(q.getWriterName())
                .writerEmail(q.getWriterEmail())
                .regDate(q.getRegDate())
                .modDate(q.getModDate())
                .answerDate(q.getAnswerDate())
                .status(q.getStatus())
                .build();

    }

    // 문의 수정
    @Transactional
    public void update (Long id, QuestionRequestDTO questionDTO, String userEmail) {

        // 글 존재 여부 확인
        QuestionBoard q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        // 권한 검증
        if (!q.getWriterEmail().equals(userEmail)) {

            throw new IllegalArgumentException("본인이 작성한 글만 수정 가능합니다.");

        }

        // 내용 수정
        q.updateContent(questionDTO.getTitle(), questionDTO.getContent());

    }

    // 답변 등록
    @Transactional
    public void updateAnswer (Long id, String answer) {

        QuestionBoard q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        q.updateAnswer(answer);

    }

    // 문의 삭제
    @Transactional
    public void delete (Long id, String userEmail) {

        // 글 존재 여부 확인
        QuestionBoard q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        // 권한 검증
        if (!q.getWriterEmail().equals(userEmail)) {

            throw new IllegalArgumentException("본인이 작성한 글만 삭제 가능합니다.");

        }

        questionRepository.deleteById(id);

    }

}
