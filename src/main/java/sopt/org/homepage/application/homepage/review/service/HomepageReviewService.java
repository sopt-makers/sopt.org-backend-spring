package sopt.org.homepage.application.homepage.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.application.admin.dto.request.main.review.AddAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.review.EditAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.response.main.review.AddAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.EditAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.GetAdminReviewResponseRecordDto;
import sopt.org.homepage.application.homepage.review.domain.HomepageReview;
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand;
import sopt.org.homepage.application.homepage.review.repository.HomepageReviewRepository;
import sopt.org.homepage.global.exception.ClientBadRequestException;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomepageReviewService {

    private final HomepageReviewRepository homepageReviewRepository;

    @Transactional(readOnly = true)
    public List<HomepageReview> findAll() {
        return homepageReviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GetAdminReviewResponseRecordDto> getReviews() {
        return homepageReviewRepository.findAllByOrderByIdAsc().stream()
                .map(GetAdminReviewResponseRecordDto::from)
                .toList();
    }

    @Transactional
    public AddAdminReviewResponseRecordDto create(AddAdminReviewRequestDto request) {
        HomepageReview review = HomepageReview.create(request.getTitle(), request.getContent(), request.getAuthorInfo());
        homepageReviewRepository.save(review);
        log.info("HomepageReview 생성 완료 - id={}", review.getId());
        return AddAdminReviewResponseRecordDto.success();
    }

    @Transactional
    public EditAdminReviewResponseRecordDto edit(Long id, EditAdminReviewRequestDto request) {
        HomepageReview review = homepageReviewRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException("HomepageReview not found with id: " + id));
        review.update(request.getTitle(), request.getContent(), request.getAuthorInfo());
        log.info("HomepageReview 수정 완료 - id={}", id);
        return EditAdminReviewResponseRecordDto.success();
    }

    @Transactional
    public void bulkCreate(BulkCreateHomepageReviewsCommand command) {
        homepageReviewRepository.deleteAll();
        List<HomepageReview> reviews = command.reviews().stream()
                .map(r -> HomepageReview.create(r.title(), r.content(), r.authorInfo()))
                .toList();
        homepageReviewRepository.saveAll(reviews);
        log.info("HomepageReview 일괄 생성 완료 - count={}", reviews.size());
    }
}
