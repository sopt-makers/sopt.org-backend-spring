package sopt.org.homepage.faq;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.faq.vo.FAQCategory;

public interface FAQRepository extends JpaRepository<FAQ, Long> {

    boolean existsByCategory(FAQCategory category);

    List<FAQ> findAllByCategoryIn(List<FAQCategory> categories);

    List<FAQ> findAllByCategoryInOrderByCategoryAsc(List<FAQCategory> categories);

    void deleteByCategoryIn(List<FAQCategory> categories);
}
