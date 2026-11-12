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
import sopt.org.homepage.faq.vo.FAQCategory;
import sopt.org.homepage.global.exception.ClientBadRequestException;

/**
 * FAQ 통합 테스트
 * <p>
 * 인수인계 목적: - FAQ는 카테고리별로 관리됨 (QUALIFICATION, INTERVIEW, ACTIVITY) - 기존 파트 FAQ는 레거시 응답(recruitQuestion)용으로 따로 관리됨 -
 * QuestionAnswer는 JSON으로 저장됨 - 동일 카테고리에 중복 FAQ 생성 불가
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
        @DisplayName("✅ 정상: 카테고리별 FAQ 생성")
        void create_Success() {
            // given
            CreateFAQCommand command = CreateFAQCommand.builder()
                    .category(FAQCategory.QUALIFICATION)
                    .questions(List.of(
                            CreateFAQCommand.QuestionAnswerCommand.builder()
                                    .question("개발 경험이 없어도 지원할 수 있나요?")
                                    .answer("네, 지원할 수 있습니다.")
                                    .build(),
                            CreateFAQCommand.QuestionAnswerCommand.builder()
                                    .question("두 개 파트에 동시에 지원할 수 있나요?")
                                    .answer("한 개 파트에만 지원할 수 있습니다.")
                                    .build()
                    ))
                    .build();

            // when
            Long id = faqService.create(command);

            // then
            assertThat(id).isNotNull();

            FAQView saved = faqService.findById(id);
            assertThat(saved.category()).isEqualTo(FAQCategory.QUALIFICATION);
            assertThat(saved.questions()).hasSize(2);
            assertThat(saved.questions().get(0).question())
                    .isEqualTo("개발 경험이 없어도 지원할 수 있나요?");
        }

        @Test
        @DisplayName("❌ 실패: 동일 카테고리 중복 생성 불가")
        void create_DuplicateCategory_ThrowsException() {
            // given - 이미 QUALIFICATION FAQ 존재
            faqRepository.save(FAQ.builder()
                    .category(FAQCategory.QUALIFICATION)
                    .questions(List.of(new FAQ.QuestionAnswer("Q", "A")))
                    .build());

            CreateFAQCommand command = CreateFAQCommand.builder()
                    .category(FAQCategory.QUALIFICATION)  // 동일 카테고리
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
        @DisplayName("✅ 정상: 레거시 파트 FAQ 일괄 교체 (신규 카테고리 FAQ 유지)")
        void replaceLegacyPartFAQs_KeepsCategoryFAQs() {
            // given - 기존 데이터
            faqRepository.save(FAQ.builder()
                    .category(FAQCategory.SERVER)
                    .questions(List.of(new FAQ.QuestionAnswer("Q1", "A1")))
                    .build());
            FAQ categoryFaq = faqRepository.save(FAQ.builder()
                    .category(FAQCategory.QUALIFICATION)
                    .questions(List.of(new FAQ.QuestionAnswer("Q2", "A2")))
                    .build());

            // when - 레거시 파트 FAQ만 교체
            BulkCreateFAQsCommand command = BulkCreateFAQsCommand.builder()
                    .faqs(List.of(
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .category("안드로이드")
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("코틀린을 몰라도 되나요?")
                                                    .answer("활동하면서 배울 수 있습니다.")
                                                    .build()
                                    ))
                                    .build()
                    ))
                    .build();

            List<Long> ids = faqService.replaceLegacyPartFAQs(command);

            // then
            assertThat(ids).hasSize(1);
            assertThat(faqService.findLegacyPartFAQs()).extracting(FAQView::category)
                    .containsExactly(FAQCategory.ANDROID);
            assertThat(faqService.findCategoryFAQs()).containsExactly(FAQView.from(categoryFaq));
            // 기존 SERVER FAQ는 삭제, QUALIFICATION FAQ는 그대로 유지됨
        }

        @Test
        @DisplayName("✅ 정상: 신규 카테고리 FAQ 일괄 교체 (레거시 파트 FAQ 유지)")
        void replaceCategoryFAQs_KeepsLegacyPartFAQs() {
            // given - 기존 데이터
            FAQ legacyPartFaq = faqRepository.save(FAQ.builder()
                    .category(FAQCategory.SERVER)
                    .questions(List.of(new FAQ.QuestionAnswer("Q1", "A1")))
                    .build());
            faqRepository.save(FAQ.builder()
                    .category(FAQCategory.QUALIFICATION)
                    .questions(List.of(new FAQ.QuestionAnswer("Q2", "A2")))
                    .build());

            // when - 신규 카테고리 FAQ만 교체
            BulkCreateFAQsCommand command = BulkCreateFAQsCommand.builder()
                    .faqs(List.of(
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .category("면접")
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("면접은 어떻게 진행되나요?")
                                                    .answer("면접 일정은 개별 안내됩니다.")
                                                    .build()
                                    ))
                                    .build(),
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .category("활동")
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("활동 기간은 얼마나 되나요?")
                                                    .answer("약 6개월입니다.")
                                                    .build()
                                    ))
                                    .build()
                    ))
                    .build();

            List<Long> ids = faqService.replaceCategoryFAQs(command);

            // then
            assertThat(ids).hasSize(2);
            assertThat(faqService.findCategoryFAQs()).extracting(FAQView::category)
                    .containsExactlyInAnyOrder(FAQCategory.INTERVIEW, FAQCategory.ACTIVITY);
            assertThat(faqService.findLegacyPartFAQs()).containsExactly(FAQView.from(legacyPartFaq));
            // 기존 QUALIFICATION FAQ는 삭제, SERVER FAQ는 그대로 유지됨
        }

        @Test
        @DisplayName("❌ 실패: 신규 카테고리 FAQ에 레거시 파트 값이 있으면 전체 롤백")
        void replaceCategoryFAQs_LegacyCategory_ThrowsException() {
            // given - 기존 데이터
            FAQ categoryFaq = faqRepository.save(FAQ.builder()
                    .category(FAQCategory.QUALIFICATION)
                    .questions(List.of(new FAQ.QuestionAnswer("Q1", "A1")))
                    .build());

            BulkCreateFAQsCommand command = BulkCreateFAQsCommand.builder()
                    .faqs(List.of(
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .category("면접")
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("새 질문")
                                                    .answer("새 답변")
                                                    .build()
                                    ))
                                    .build(),
                            BulkCreateFAQsCommand.FAQData.builder()
                                    .category("안드로이드")  // 레거시 파트 값
                                    .question(List.of(
                                            BulkCreateFAQsCommand.QuestionData.builder()
                                                    .question("새 질문")
                                                    .answer("새 답변")
                                                    .build()
                                    ))
                                    .build()
                    ))
                    .build();

            // when & then
            assertThatThrownBy(() -> faqService.replaceCategoryFAQs(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown FAQ category");

            assertThat(faqService.findCategoryFAQs()).containsExactly(FAQView.from(categoryFaq));
            // 기존 QUALIFICATION FAQ는 그대로 유지됨
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("FAQ 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 레거시 파트 FAQ만 기존 순서대로 조회")
        void findLegacyPartFAQs_ReturnsLegacyOnly() {
            // given
            faqRepository.saveAll(List.of(
                    FAQ.builder().category(FAQCategory.WEB).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().category(FAQCategory.QUALIFICATION).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().category(FAQCategory.ANDROID).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().category(FAQCategory.SERVER).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build()
            ));

            // when
            List<FAQView> result = faqService.findLegacyPartFAQs();

            // then - 파트 알파벳 순서: ANDROID < SERVER < WEB
            assertThat(result).extracting(FAQView::category)
                    .containsExactly(FAQCategory.ANDROID, FAQCategory.SERVER, FAQCategory.WEB);
        }

        @Test
        @DisplayName("🔍 조회: 신규 카테고리 FAQ만 조회")
        void findCategoryFAQs_ReturnsCategoriesOnly() {
            // given
            faqRepository.saveAll(List.of(
                    FAQ.builder().category(FAQCategory.ACTIVITY).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().category(FAQCategory.ANDROID).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().category(FAQCategory.QUALIFICATION).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build(),
                    FAQ.builder().category(FAQCategory.INTERVIEW).questions(List.of(new FAQ.QuestionAnswer("Q", "A"))).build()
            ));

            // when
            List<FAQView> result = faqService.findCategoryFAQs();

            // then
            assertThat(result).extracting(FAQView::category)
                    .containsExactlyInAnyOrder(FAQCategory.QUALIFICATION, FAQCategory.INTERVIEW, FAQCategory.ACTIVITY);
        }

        @Test
        @DisplayName("🔍 조회: 빈 결과")
        void findCategoryFAQs_Empty() {
            // when
            List<FAQView> result = faqService.findCategoryFAQs();

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
        @DisplayName("📌 규칙: FAQ는 category 필수")
        void faq_RequiresCategory() {
            // when & then
            assertThatThrownBy(() -> FAQ.builder()
                    .category(null)
                    .questions(List.of(new FAQ.QuestionAnswer("Q", "A")))
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("FAQ category");
        }
    }
}
