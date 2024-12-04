package sopt.org.homepage.review;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.mapper.ResponseMapper;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;
import sopt.org.homepage.review.repository.ReviewQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewQueryRepository reviewQueryRepository;
    private final ResponseMapper responseMapper;

    public PaginateResponseDto<ReviewsResponseDto> getReviews(ReviewsRequestDto requestDto) {
        // 조건에 맞는 전체 리뷰 수 조회
        long totalCount = reviewQueryRepository.countWithFilters(requestDto);

        // 페이지네이션 된 리뷰 목록 조회
        var reviews = reviewQueryRepository.findAllWithFilters(
                requestDto,
                requestDto.getOffset(),
                requestDto.getLimit()
        );

        // Entity를 DTO로 변환
        var reviewDtos = reviews.stream()
                .map(responseMapper::toReviewResponseDto)
                .toList();

        // 페이지네이션 응답 생성
        return new PaginateResponseDto<>(
                reviewDtos,
                (int) totalCount,
                requestDto.getLimit(),
                requestDto.getPageNo()
        );
    }
}
