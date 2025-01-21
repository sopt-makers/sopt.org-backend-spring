package sopt.org.homepage.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.mapper.ResponseMapper;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;
import sopt.org.homepage.review.repository.ReviewQueryRepository;
import sopt.org.homepage.review.repository.ReviewRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewRepository reviewRepository;
    private final ResponseMapper responseMapper;

    @Override
    public PaginateResponseDto<ReviewsResponseDto> getReviews(ReviewsRequestDto requestDto) {
        long totalCount = reviewQueryRepository.countWithFilters(requestDto);

        var reviews = reviewQueryRepository.findAllWithFilters(
                requestDto,
                requestDto.getOffset(),
                requestDto.getLimit()
        );

        var reviewDtos = reviews.stream()
                .map(responseMapper::toReviewResponseDto)
                .toList();

        return new PaginateResponseDto<>(
                reviewDtos,
                (int) totalCount,
                requestDto.getLimit(),
                requestDto.getPageNo()
        );
    }

    @Override
    public List<ReviewsResponseDto> getRandomReviewByPart() {
        return Arrays.stream(Part.values())
                .map(reviewQueryRepository::findRandomReviewByPart)
                .filter(Objects::nonNull)
                .map(responseMapper::toReviewResponseDto)
                .collect(Collectors.toList());
    }
}
