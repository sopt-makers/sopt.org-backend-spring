package sopt.org.homepage.review.service;

import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;

import java.util.List;

public interface ReviewService {
    PaginateResponseDto<ReviewsResponseDto> getReviews(ReviewsRequestDto requestDto);
    List<ReviewsResponseDto> getRandomReviewByPart();
}