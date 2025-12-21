package sopt.org.homepage.faq.repository.query;

import java.util.List;
import java.util.Optional;
import sopt.org.homepage.faq.domain.FAQ;
import sopt.org.homepage.global.common.type.PartType;

/**
 * FAQQueryRepository
 * <p>
 * 책임: FAQ 조회 쿼리
 */
public interface FAQQueryRepository {

    /**
     * ID로 조회
     */
    Optional<FAQ> findById(Long id);

    /**
     * 특정 파트의 FAQ 조회
     */
    Optional<FAQ> findByPart(PartType part);

    /**
     * 모든 FAQ 조회
     */
    List<FAQ> findAll();

    /**
     * FAQ 존재 여부 확인
     */
    boolean existsByPart(PartType part);
}
