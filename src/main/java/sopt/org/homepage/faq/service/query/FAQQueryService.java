package sopt.org.homepage.faq.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.faq.repository.query.FAQQueryRepository;
import sopt.org.homepage.faq.service.query.dto.FAQView;

import java.util.List;

/**
 * FAQQueryService
 *
 * 책임: FAQ 조회 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FAQQueryService {

    private final FAQQueryRepository faqQueryRepository;

    /**
     * 특정 FAQ 상세 조회
     */
    public FAQView getFAQDetail(Long faqId) {
        log.debug("Querying FAQ detail: {}", faqId);

        return faqQueryRepository.findById(faqId)
                .map(FAQView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ %d not found", faqId)
                ));
    }

    /**
     * 특정 파트의 FAQ 조회
     */
    public FAQView getFAQByPart(PartType part) {
        log.debug("Querying FAQ for part: {}", part);

        return faqQueryRepository.findByPart(part)
                .map(FAQView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("FAQ for %s not found", part)
                ));
    }

    /**
     * 모든 FAQ 조회
     */
    public List<FAQView> getAllFAQs() {
        log.debug("Querying all FAQs");

        return faqQueryRepository.findAll()
                .stream()
                .map(FAQView::from)
                .toList();
    }

    /**
     * 특정 파트의 FAQ 존재 여부 확인
     */
    public boolean existsByPart(PartType part) {
        return faqQueryRepository.existsByPart(part);
    }
}