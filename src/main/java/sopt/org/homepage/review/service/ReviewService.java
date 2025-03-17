package sopt.org.homepage.review.service;

import java.util.List;

import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.review.dto.request.AddReviewRequestDto;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;

public interface ReviewService {
	PaginateResponseDto<ReviewsResponseDto> getReviews(ReviewsRequestDto requestDto);

	List<ReviewsResponseDto> getRandomReviewByPart();

	void addReview(AddReviewRequestDto request);
}
