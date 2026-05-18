package sopt.org.homepage.application.homepage.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.application.homepage.review.domain.HomepageReview;
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand;
import sopt.org.homepage.application.homepage.review.repository.HomepageReviewRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomepageReviewService {

    private final HomepageReviewRepository homepageReviewRepository;

    @Transactional
    public void bulkCreate(BulkCreateHomepageReviewsCommand command) {
        List<HomepageReview> reviews = command.reviews().stream()
                .map(r -> HomepageReview.create(r.title(), r.content(), r.authorInfo()))
                .toList();

        homepageReviewRepository.saveAll(reviews);

        log.info("HomepageReview 일괄 생성 완료 - count={}", reviews.size());
    }
}
