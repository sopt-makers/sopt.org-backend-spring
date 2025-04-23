package sopt.org.homepage.review.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.mapper.ResponseMapper;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.review.dto.request.AddReviewMainCategory;
import sopt.org.homepage.review.dto.request.AddReviewRequestDto;
import sopt.org.homepage.review.dto.request.AddReviewSubActivity;
import sopt.org.homepage.review.dto.request.AddReviewSubRecruiting;
import sopt.org.homepage.review.dto.request.ReviewsRequestDto;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;
import sopt.org.homepage.review.entity.ReviewEntity;
import sopt.org.homepage.review.repository.ReviewQueryRepository;
import sopt.org.homepage.review.repository.ReviewRepository;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.scrap.service.ScraperService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

	private final ReviewQueryRepository reviewQueryRepository;
	private final ReviewRepository reviewRepository;
	private final ResponseMapper responseMapper;
	private final ScraperService scraperService;

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
			(int)totalCount,
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

	@Override
	@Transactional
	public void addReview(AddReviewRequestDto dto) {
		if (reviewRepository.existsByUrl(dto.getLink())) {
			throw new BusinessLogicException("이미 등록된 활동후기입니다.");
		}

		CreateScraperResponseDto scrapResult = scraperService.scrap(new ScrapArticleDto(dto.getLink()));

		List<String> subjects = getReviewSubject(dto.getMainCategory(), dto.getSubActivities(), dto.getSubRecruiting());

		ReviewEntity review = ReviewEntity.builder()
			.thumbnailUrl(scrapResult.getThumbnailUrl())
			.title(scrapResult.getTitle())
			.description(scrapResult.getDescription())
			.platform(scrapResult.getPlatform())
			.url(dto.getLink())
			.category(dto.getMainCategory().getValue())
			.subject(subjects)
			.author(dto.getAuthor())
			.part(dto.getPart())
			.authorProfileImageUrl(dto.getAuthorProfileImageUrl())
			.generation(dto.getGeneration())
			.build();

		reviewRepository.save(review);
	}

	private List<String> getReviewSubject(AddReviewMainCategory mainCategory, List<AddReviewSubActivity> subActivities,
		AddReviewSubRecruiting subRecruiting) {
		List<String> subjects = new ArrayList<>();

		if (mainCategory == AddReviewMainCategory.ACTIVITY) {
			if (subActivities == null || subActivities.isEmpty()) {
				throw new ClientBadRequestException("전체활동 카테고리의 세부 활동 유형을 선택해주세요.");
			}
			for (AddReviewSubActivity activity : subActivities) {
				subjects.add(activity.getValue());
			}
		} else if (mainCategory == AddReviewMainCategory.RECRUITING) {
			if (subRecruiting == null) {
				throw new ClientBadRequestException("서류/면접 카테고리의 세부 유형을 선택해주세요.");
			}
			subjects.add(subRecruiting.getValue());
		}

		return subjects;
	}
}
