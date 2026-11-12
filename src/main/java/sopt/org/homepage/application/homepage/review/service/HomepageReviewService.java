package sopt.org.homepage.application.homepage.review.service;

import java.util.List;
import java.util.Map;
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
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand.ReviewData;
import sopt.org.homepage.application.homepage.review.repository.HomepageReviewRepository;
import sopt.org.homepage.global.exception.ClientBadRequestException;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomepageReviewService {

    public static final int MAX_REVIEW_PER_TYPE = 7;

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
        HomepageReviewType type = resolveType(request.getType());
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
     * 후기 전체 교체
     * <p>
     * 요청 목록이 곧 전체 후기 목록이 된다. 빈 목록이면 모든 후기를 삭제한다.
     */
    @Transactional
    public void bulkCreate(BulkCreateHomepageReviewsCommand command) {
        validateMaxCountPerType(command.reviews().stream().map(ReviewData::type).toList());

        List<HomepageReview> reviews = command.reviews().stream()
                .map(r -> HomepageReview.create(resolveType(r.type()), r.title(), r.content(), r.authorInfo()))
                .toList();

        homepageReviewRepository.deleteAll();
        homepageReviewRepository.saveAll(reviews);
        log.info("HomepageReview 일괄 생성 완료 - count={}", reviews.size());
    }

    /**
     * 일괄 교체할 후기 목록의 종류별 개수 검증 (DB 상태와 무관하게 요청 목록만 검사)
     * <p>
     * 배포 1단계에서 미리 검증해 잘못된 데이터가 캐시에 저장되지 않도록 한다.
     *
     * @param types 각 후기의 종류 (null은 활동 후기로 간주)
     */
    public void validateMaxCountPerType(List<HomepageReviewType> types) {
        Map<HomepageReviewType, Long> countByType = types.stream()
                .map(HomepageReviewService::resolveType)
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        countByType.forEach((type, count) -> {
            if (count > MAX_REVIEW_PER_TYPE) {
                throw exceeded(type);
            }
        });
    }

    /**
     * 개수 조회 후 저장하므로 동시 요청 시 최대 개수를 넘을 수 있다. 어드민 전용 기능이라 허용한다.
     */
    private void validateCapacity(HomepageReviewType type) {
        if (homepageReviewRepository.countByType(type) >= MAX_REVIEW_PER_TYPE) {
            throw exceeded(type);
        }
    }

    /**
     * 종류가 지정되지 않은 경우 활동 후기로 간주 (type 필드 도입 이전 요청 호환)
     */
    private static HomepageReviewType resolveType(HomepageReviewType type) {
        return type != null ? type : HomepageReviewType.ACTIVITY;
    }

    private static ClientBadRequestException exceeded(HomepageReviewType type) {
        return new ClientBadRequestException(
                type.getDisplayName() + "는 최대 " + MAX_REVIEW_PER_TYPE + "개까지 등록 가능합니다.");
    }
}
