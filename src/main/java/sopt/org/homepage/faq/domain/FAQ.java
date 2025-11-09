package sopt.org.homepage.faq.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import sopt.org.homepage.common.type.PartType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FAQ 애그리거트 루트
 *
 * 책임:
 * - 파트별 FAQ 관리
 * - 질문과 답변 관리
 * - Generation과 독립적으로 관리
 */
@Entity
@Table(name = "\"FAQ\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"part\"", nullable = false, length = 20)
    private PartType part;  // ANDROID, IOS, WEB, SERVER, PLAN, DESIGN, COMMON

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"questions\"", nullable = false, columnDefinition = "text")
    private List<QuestionAnswer> questions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public FAQ(PartType part, List<QuestionAnswer> questions) {
        validatePart(part);
        validateQuestions(questions);

        this.part = part;
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
    }

    // === 비즈니스 메서드 ===

    /**
     * 질문 목록 전체 교체
     */
    public void updateQuestions(List<QuestionAnswer> questions) {
        validateQuestions(questions);
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
    }

    /**
     * 질문 추가
     */
    public void addQuestion(QuestionAnswer questionAnswer) {
        validateQuestionAnswer(questionAnswer);
        this.questions.add(questionAnswer);
    }

    /**
     * 특정 인덱스의 질문 수정
     */
    public void updateQuestion(int index, QuestionAnswer questionAnswer) {
        validateIndex(index);
        validateQuestionAnswer(questionAnswer);
        this.questions.set(index, questionAnswer);
    }

    /**
     * 특정 인덱스의 질문 삭제
     */
    public void removeQuestion(int index) {
        validateIndex(index);
        this.questions.remove(index);
    }

    /**
     * 질문 개수 조회
     */
    public int getQuestionCount() {
        return this.questions.size();
    }

    // === Validation ===

    private void validatePart(PartType part) {
        if (part == null) {
            throw new IllegalArgumentException("Part must not be null");
        }
    }

    private void validateQuestions(List<QuestionAnswer> questions) {
        if (questions == null) {
            return;  // null은 빈 리스트로 처리
        }

        for (QuestionAnswer qa : questions) {
            validateQuestionAnswer(qa);
        }
    }

    private void validateQuestionAnswer(QuestionAnswer qa) {
        if (qa == null) {
            throw new IllegalArgumentException("QuestionAnswer must not be null");
        }
        if (qa.question() == null || qa.question().isBlank()) {
            throw new IllegalArgumentException("Question must not be blank");
        }
        if (qa.answer() == null || qa.answer().isBlank()) {
            throw new IllegalArgumentException("Answer must not be blank");
        }
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= questions.size()) {
            throw new IllegalArgumentException(
                    String.format("Invalid index: %d. Valid range: 0-%d", index, questions.size() - 1)
            );
        }
    }

    /**
     * QuestionAnswer Record (JSON에 저장될 Q&A)
     */
    public record QuestionAnswer(
            String question,
            String answer
    ) {
        public QuestionAnswer {
            if (question == null || question.isBlank()) {
                throw new IllegalArgumentException("Question must not be blank");
            }
            if (answer == null || answer.isBlank()) {
                throw new IllegalArgumentException("Answer must not be blank");
            }
        }
    }
}