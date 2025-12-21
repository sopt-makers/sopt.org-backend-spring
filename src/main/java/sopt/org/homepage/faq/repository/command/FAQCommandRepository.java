package sopt.org.homepage.faq.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.faq.domain.FAQ;
import sopt.org.homepage.global.common.type.PartType;

/**
 * FAQCommandRepository
 * <p>
 * 책임: FAQ 엔티티의 생성, 수정, 삭제
 */
public interface FAQCommandRepository extends JpaRepository<FAQ, Long> {

    /**
     * 특정 파트의 FAQ 삭제
     */
    void deleteByPart(PartType part);
}
