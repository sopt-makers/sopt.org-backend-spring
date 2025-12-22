package sopt.org.homepage.faq;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.faq.dto.BulkCreateFAQsCommand;
import sopt.org.homepage.faq.dto.CreateFAQCommand;
import sopt.org.homepage.faq.dto.FAQView;
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
        if (faqRepository.existsByPart(command.part())) {
            throw new ClientBadRequestException(
                    String.format("FAQ for %s already exists", command.part())
            );
        }
        FAQ faq = command.toEntity();
        return faqRepository.save(faq).getId();
    }

    @Transactional
    public List<Long> bulkCreate(BulkCreateFAQsCommand command) {
        faqRepository.deleteAll();

        List<FAQ> faqs = command.faqs().stream()
                .map(data -> {
                    PartType part = PartType.fromString(data.part());
                    List<FAQ.QuestionAnswer> questions = data.question().stream()
                            .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                            .toList();
                    return FAQ.builder().part(part).questions(questions).build();
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
    public List<FAQView> findAll() {
        return faqRepository.findAllOrderByPart().stream()
                .map(FAQView::from)
                .toList();
    }

}
