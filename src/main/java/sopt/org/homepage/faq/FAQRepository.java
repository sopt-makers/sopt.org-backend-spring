package sopt.org.homepage.faq;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sopt.org.homepage.global.common.type.PartType;

public interface FAQRepository extends JpaRepository<FAQ, Long> {

    boolean existsByPart(PartType part);

    @Query("SELECT f FROM FAQ f ORDER BY f.part ASC")
    List<FAQ> findAllOrderByPart();
}
