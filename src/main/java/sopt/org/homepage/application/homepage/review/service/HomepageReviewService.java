package sopt.org.homepage.application.homepage.review.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import sopt.org.homepage.application.homepage.review.domain.HomepageReviewType;
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand;
import sopt.org.homepage.application.homepage.review.repository.HomepageReviewRepository;
import sopt.org.homepage.global.exception.ClientBadRequestException;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomepageReviewService {

    private static final int MAX_REVIEW_PER_TYPE = HomepageReviewType.MAX_COUNT_PER_TYPE;

    private final HomepageReviewRepository homepageReviewRepository;

    @Transactional(readOnly = true)
    public List<HomepageReview> findAll() {
        return homepageReviewRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public List<GetAdminReviewResponseRecordDto> getReviews() {
        return homepageReviewRepository.findAllByOrderByIdAsc().stream()
                .map(GetAdminReviewResponseRecordDto::from)
                .toList();
    }

    @Transactional
    public AddAdminReviewResponseRecordDto create(AddAdminReviewRequestDto request) {
        HomepageReviewType type = HomepageReviewType.orDefault(request.getType());
        validateCapacity(type);

        HomepageReview review = HomepageReview.create(type, request.getTitle(), request.getContent(), request.getAuthorInfo());
        homepageReviewRepository.save(review);
        log.info("HomepageReview 생성 완료 - id={}, type={}", review.getId(), type);
        return AddAdminReviewResponseRecordDto.success();
    }

    @Transactional
    public EditAdminReviewResponseRecordDto edit(Long id, EditAdminReviewRequestDto request) {
        HomepageReview review = homepageReviewRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException("HomepageReview not found with id: " + id));

        HomepageReviewType newType = request.getType();
        if (newType != null && newType != review.getType()) {
            validateCapacity(newType);
        }

        review.update(newType, request.getTitle(), request.getContent(), request.getAuthorInfo());
        log.info("HomepageReview 수정 완료 - id={}, type={}", id, review.getType());
        return EditAdminReviewResponseRecordDto.success();
    }

    /**
     * 후기 일괄 교체
     * <p>
     * - 요청에 포함된 종류(type)의 후기만 삭제 후 재생성하고, 포함되지 않은 종류는 유지한다.
     * - 빈 목록이면 모든 후기를 삭제한다. (type 도입 이전의 전체 교체 동작 호환)
     */
    @Transactional
    public void bulkCreate(BulkCreateHomepageReviewsCommand command) {
        List<HomepageReview> reviews = command.reviews().stream()
                .map(r -> HomepageReview.create(r.type(), r.title(), r.content(), r.authorInfo()))
                .toList();

        if (reviews.isEmpty()) {
            homepageReviewRepository.deleteAll();
            log.info("HomepageReview 전체 삭제 완료");
            return;
        }

        Map<HomepageReviewType, Long> countByType = reviews.stream()
                .collect(Collectors.groupingBy(HomepageReview::getType, Collectors.counting()));
        countByType.forEach((type, count) -> {
            if (count > MAX_REVIEW_PER_TYPE) {
                throw new ClientBadRequestException(
                        type.getDisplayName() + "는 최대 " + MAX_REVIEW_PER_TYPE + "개까지 등록 가능합니다. count=" + count);
            }
        });

        Set<HomepageReviewType> types = countByType.keySet();
        homepageReviewRepository.deleteByTypeIn(types);
        homepageReviewRepository.saveAll(reviews);
        log.info("HomepageReview 일괄 생성 완료 - types={}, count={}", types, reviews.size());
    }

    private void validateCapacity(HomepageReviewType type) {
        if (homepageReviewRepository.countByType(type) >= MAX_REVIEW_PER_TYPE) {
            throw new ClientBadRequestException(
                    type.getDisplayName() + "는 최대 " + MAX_REVIEW_PER_TYPE + "개까지 등록 가능합니다.");
        }
    }
}
