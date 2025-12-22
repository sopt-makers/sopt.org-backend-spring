package sopt.org.homepage.faq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.faq.dto.BulkCreateFAQsCommand;
import sopt.org.homepage.faq.dto.CreateFAQCommand;
import sopt.org.homepage.faq.dto.FAQView;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.global.exception.ClientBadRequestException;

/**
 * FAQ 통합 테스트
 * <p>
 * 인수인계 목적: - FAQ는 파트별로 관리됨 (ANDROID, IOS, WEB, SERVER, PLAN, DESIGN, COMMON) - QuestionAnswer는 JSON으로 저장됨 - 동일 파트에 중복
 * FAQ 생성 불가
 */
@DisplayName("FAQ 서비스 통합 테스트")
class FAQServiceTest extends IntegrationTestBase {

    @Autowired
    private FAQService faqService;

    @Autowired
    private FAQRepository faqRepository;

    @AfterEach
    void tearDown() {
        faqRepository.deleteAll();
    }

    // ===== 생성 시나리오 =====

    @Nested
    @DisplayName("FAQ 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: 파트별 FAQ 생성")
        void create_Success() {
            // given
            CreateFAQCommand command = CreateFAQCommand.builder()
                    .part(PartType.ANDROID)
                    .questions(List.of(
                            CreateFAQCommand.QuestionAnswerCommand.builder()
                                    .question("안드로이드 개발 경험이 필수인가요?")
                                    .answer("필수는 아니지만, 기본적인 프로그래밍 이해가 필요합니다.")
                                    .build(),
                            CreateFAQCommand.QuestionAnswerCommand.builder()
                                    .question("코틀린을 몰라도 되나요?")
                                    .answer("활동하면서 배울 수 있습니다.")
                                    .build()
                    ))
                    .build();

            // when
            Long id = faqService.create(command);

            // then
            assertThat(id).isNotNull();

            FAQView saved = faqService.findById(id);
            assertThat(saved.part()).isEqualTo(PartType.ANDROID);
            assertThat(saved.questions()).hasSize(2);
            assertThat(saved.questions().get(0).question())
                    .isEqualTo("안드로이드 개발 경험이 필수인가요?");
        }

        @Test
        @DisplayName("❌ 실패: 동일 파트 중복 생성 불가")
        void create_DuplicatePart_ThrowsException() {
            // given - 이미 ANDROID FAQ 존재
            faqRepository.save(FAQ.builder()
                    .part(PartType.ANDROID)
                    .questions(List.of(new FAQ.QuestionAnswer("Q", "A")))
                    .build());

            CreateFAQCommand command = CreateFAQCommand.builder()
                    .part(PartType.ANDROID)  // 동일 파트
                    .questions(List.of(
                            CreateFAQCommand.QuestionAnswerCommand.builder()
                                    .question("새 질문")
                                    .answer("새 답변")
                                    .build()
                    ))
                    .build();

            // when & then
            assertThatThrownBy(() -> faqService.create(command))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("✅ 정상: 일괄 생성 (기존 데이터 전체 교체)")
        void bulkCreate_ReplacesAllExisting() {
            // given - 기존 데이터
            faqRepository.saveAll(List.of(
                    FAQ.builder().part(PartType.ANDROID).questions(List.of(new FAQ.QuestionAnswer("Q1", "A1"))).build(),
                    FAQ.builder().part(PartType.IOS).questions(List.of(new FAQ.QuestionAnswer("Q2", "A2"))).build()
            ));
            assertThat(faqRepository.count()).isEqualTo(2);

            // when - 새로운 데이터로 전체 교체
            BulkCreateFAQsCommand command = BulkCreateFAQsCommand.builder()
                    .faqs(List.of(
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .part("SERVER")
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("서버 개발 언어는?")
                                                    .answer("Spring Boot를 주로 사용합니다.")
                                                    .build()
                                    ))
                                    .build(),
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .part("WEB")
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("React를 사용하나요?")
                                                    .answer("네, React를 주로 사용합니다.")
                                                    .build()
                                    ))
                                    .build()
                    ))
                    .build();

            List<Long> ids = faqService.bulkCreate(command);

            // then
            assertThat(ids).hasSize(2);

            List<FAQView> result = faqService.findAll();
            assertThat(result).hasSize(2);
            assertThat(result).extracting(FAQView::part)
                    .containsExactlyInAnyOrder(PartType.SERVER, PartType.WEB);
            // 기존 ANDROID, IOS FAQ는 삭제됨
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("FAQ 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 전체 FAQ 조회 (파트 순서대로)")
        void findAll_OrderedByPart() {
            // given
            faqRepository.saveAll(List.of(
                    FAQ.builder().part(PartType.WEB).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().part(PartType.ANDROID).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().part(PartType.SERVER).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build()
            ));

            // when
            List<FAQView> result = faqService.findAll();

            // then
            assertThat(result).hasSize(3);
            // 파트 알파벳 순서: ANDROID < SERVER < WEB
        }

        @Test
        @DisplayName("🔍 조회: 빈 결과")
        void findAll_Empty() {
            // when
            List<FAQView> result = faqService.findAll();

            // then
            assertThat(result).isEmpty();
        }


        @Test
        @DisplayName("❌ 조회: 존재하지 않는 ID")
        void findById_NotFound_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> faqService.findById(999L))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("not found");
        }
    }

    // ===== 비즈니스 규칙 시나리오 =====

    @Nested
    @DisplayName("비즈니스 규칙")
    class BusinessRules {

        @Test
        @DisplayName("📌 규칙: QuestionAnswer는 question, answer 모두 필수")
        void questionAnswer_RequiresBothFields() {
            // when & then - 빈 question
            assertThatThrownBy(() -> new FAQ.QuestionAnswer("", "답변"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Question");

            // 빈 answer
            assertThatThrownBy(() -> new FAQ.QuestionAnswer("질문", ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Answer");

            // null question
            assertThatThrownBy(() -> new FAQ.QuestionAnswer(null, "답변"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("📌 규칙: FAQ는 part 필수")
        void faq_RequiresPart() {
            // when & then
            assertThatThrownBy(() -> FAQ.builder()
                    .part(null)
                    .questions(List.of(new FAQ.QuestionAnswer("Q", "A")))
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Part");
        }
    }
}
