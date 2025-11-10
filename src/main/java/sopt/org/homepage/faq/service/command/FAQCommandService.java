package sopt.org.homepage.faq.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.faq.domain.FAQ;
import sopt.org.homepage.faq.repository.command.FAQCommandRepository;
import sopt.org.homepage.faq.repository.query.FAQQueryRepository;
import sopt.org.homepage.faq.service.command.dto.BulkCreateFAQsCommand;
import sopt.org.homepage.faq.service.command.dto.CreateFAQCommand;
import sopt.org.homepage.faq.service.command.dto.UpdateFAQCommand;

import java.util.List;

/**
 * FAQCommandService
 *
 * 책임: FAQ 엔티티의 생성, 수정, 삭제 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FAQCommandService {

    private final FAQCommandRepository faqCommandRepository;
    private final FAQQueryRepository faqQueryRepository;

    /**
     * FAQ 생성
     */
    public Long createFAQ(CreateFAQCommand command) {
        log.info("Creating FAQ for part: {}", command.part());

        // 중복 검증
        if (faqQueryRepository.existsByPart(command.part())) {
            throw new ClientBadRequestException(
                    String.format("FAQ for %s already exists", command.part())
            );
        }

        FAQ faq = command.toEntity();
        FAQ saved = faqCommandRepository.save(faq);

        log.info("FAQ created: {}", saved.getId());
        return saved.getId();
    }

    /**
     * FAQ 일괄 생성 (기존 데이터 모두 삭제 후 재생성)
     */
    public List<Long> bulkCreateFAQs(BulkCreateFAQsCommand command) {
        log.info("Bulk creating FAQs");

        // 1. 기존 FAQ 모두 삭제
        faqCommandRepository.deleteAll();

        // 2. 새로운 FAQ 생성
        List<FAQ> faqs = command.faqs().stream()
                .map(data -> {
                    PartType part = PartType.fromString(data.part());
                    List<FAQ.QuestionAnswer> questions = data.question().stream()
                            .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                            .toList();

                    return FAQ.builder()
                            .part(part)
                            .questions(questions)
                            .build();
                })
                .toList();

        List<FAQ> saved = faqCommandRepository.saveAll(faqs);

        log.info("Bulk created {} FAQs", saved.size());

        return saved.stream().map(FAQ::getId).toList();
    }

    /**
     * FAQ 수정
     */
    public void updateFAQ(UpdateFAQCommand command) {
        log.info("Updating FAQ: {}", command.id());

        FAQ faq = faqQueryRepository.findById(command.id())
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ %d not found", command.id())
                ));

        List<FAQ.QuestionAnswer> questions = command.questions().stream()
                .map(q -> new FAQ.QuestionAnswer(q.question(), q.answer()))
                .toList();

        faq.updateQuestions(questions);

        log.info("FAQ updated: {}", command.id());
    }

    /**
     * 질문 추가
     */
    public void addQuestion(Long faqId, String question, String answer) {
        log.info("Adding question to FAQ: {}", faqId);

        FAQ faq = faqQueryRepository.findById(faqId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ %d not found", faqId)
                ));

        faq.addQuestion(new FAQ.QuestionAnswer(question, answer));

        log.info("Question added to FAQ: {}", faqId);
    }

    /**
     * 특정 인덱스의 질문 수정
     */
    public void updateQuestion(Long faqId, int index, String question, String answer) {
        log.info("Updating question {} in FAQ: {}", index, faqId);

        FAQ faq = faqQueryRepository.findById(faqId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ %d not found", faqId)
                ));

        faq.updateQuestion(index, new FAQ.QuestionAnswer(question, answer));

        log.info("Question {} updated in FAQ: {}", index, faqId);
    }

    /**
     * 특정 인덱스의 질문 삭제
     */
    public void removeQuestion(Long faqId, int index) {
        log.info("Removing question {} from FAQ: {}", index, faqId);

        FAQ faq = faqQueryRepository.findById(faqId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ %d not found", faqId)
                ));

        faq.removeQuestion(index);

        log.info("Question {} removed from FAQ: {}", index, faqId);
    }

    /**
     * FAQ 삭제
     */
    public void deleteFAQ(Long faqId) {
        log.info("Deleting FAQ: {}", faqId);

        if (!faqQueryRepository.findById(faqId).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("FAQ %d not found", faqId)
            );
        }

        faqCommandRepository.deleteById(faqId);

        log.info("FAQ deleted: {}", faqId);
    }

    /**
     * 특정 파트의 FAQ 삭제
     */
    public void deleteByPart(PartType part) {
        log.info("Deleting FAQ for part: {}", part);

        faqCommandRepository.deleteByPart(part);

        log.info("FAQ deleted for part: {}", part);
    }
}