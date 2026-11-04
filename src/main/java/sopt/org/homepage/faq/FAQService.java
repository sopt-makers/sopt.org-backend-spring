package sopt.org.homepage.faq;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.faq.dto.BulkCreateFAQsCommand;
import sopt.org.homepage.faq.dto.CreateFAQCommand;
import sopt.org.homepage.faq.dto.FAQView;
import sopt.org.homepage.faq.vo.FAQCategory;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.global.exception.ClientBadRequestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;

    // ===== Command =====

    @Transactional
    public Long create(CreateFAQCommand command) {
        if (faqRepository.existsByCategory(command.category())) {
            throw new ClientBadRequestException(
                    String.format("FAQ for %s already exists", command.category())
            );
        }
        FAQ faq = command.toEntity();
        return faqRepository.save(faq).getId();
    }

    /**
     * 레거시 파트 FAQ만 교체 (신규 카테고리 FAQ는 유지)
     */
    @Transactional
    public List<Long> replaceLegacyPartFAQs(BulkCreateFAQsCommand command) {
        faqRepository.deleteByCategoryIn(FAQCategory.legacyValues());

        List<FAQ> faqs = command.faqs().stream()
                .map(data -> {
                    FAQCategory category = FAQCategory.fromPartType(PartType.fromString(data.category()));
                    List<FAQ.QuestionAnswer> questions = data.question().stream()
                            .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                            .toList();
                    return FAQ.builder().category(category).questions(questions).build();
                })
                .toList();

        return faqRepository.saveAll(faqs).stream().map(FAQ::getId).toList();
    }

    /**
     * 신규 카테고리 FAQ만 교체 (레거시 파트 FAQ는 유지)
     */
    @Transactional
    public List<Long> replaceCategoryFAQs(BulkCreateFAQsCommand command) {
        faqRepository.deleteByCategoryIn(FAQCategory.currentValues());

        List<FAQ> faqs = command.faqs().stream()
                .map(data -> {
                    FAQCategory category = FAQCategory.fromString(data.category());
                    List<FAQ.QuestionAnswer> questions = data.question().stream()
                            .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                            .toList();
                    return FAQ.builder().category(category).questions(questions).build();
                })
                .toList();

        return faqRepository.saveAll(faqs).stream().map(FAQ::getId).toList();
    }

    // ===== Query =====

    @Transactional(readOnly = true)
    public FAQView findById(Long id) {
        return faqRepository.findById(id)
                .map(FAQView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ %d not found", id)
                ));
    }


    @Transactional(readOnly = true)
    public List<FAQView> findLegacyPartFAQs() {
        return faqRepository.findAllByCategoryInOrderByCategoryAsc(FAQCategory.legacyValues()).stream()
                .map(FAQView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FAQView> findCategoryFAQs() {
        return faqRepository.findAllByCategoryIn(FAQCategory.currentValues()).stream()
                .sorted(Comparator.comparing(FAQ::getCategory))
                .map(FAQView::from)
                .toList();
    }

}
