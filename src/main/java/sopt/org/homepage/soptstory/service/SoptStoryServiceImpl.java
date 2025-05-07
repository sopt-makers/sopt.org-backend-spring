package sopt.org.homepage.soptstory.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.scrap.service.ScraperService;
import sopt.org.homepage.soptstory.dto.request.CreateSoptStoryDto;
import sopt.org.homepage.soptstory.dto.request.GetSoptStoryListRequestDto;
import sopt.org.homepage.soptstory.dto.response.CreateSoptStoryResponseDto;
import sopt.org.homepage.soptstory.dto.response.LikeSoptStoryResponseDto;
import sopt.org.homepage.soptstory.dto.response.SoptStoryResponseDto;
import sopt.org.homepage.soptstory.entity.SoptStoryEntity;
import sopt.org.homepage.soptstory.entity.SoptStoryLikeEntity;
import sopt.org.homepage.soptstory.repository.SoptStoryLikeRepository;
import sopt.org.homepage.soptstory.repository.SoptStoryQueryRepository;
import sopt.org.homepage.soptstory.repository.SoptStoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptStoryServiceImpl implements SoptStoryService {
	private final SoptStoryRepository soptStoryRepository;
	private final SoptStoryQueryRepository soptStoryQueryRepository;
	private final SoptStoryLikeRepository soptStoryLikeRepository;
	private final ScraperService scraperService;

	@Override
	public PaginateResponseDto<SoptStoryResponseDto> paginateSoptStorys(GetSoptStoryListRequestDto requestDto,
																	   String sessionId) {
		var soptStorys = soptStoryQueryRepository.findAllSorted(requestDto);
		var totalCount = (int) soptStoryRepository.count();

		var likedSoptStorys = soptStoryLikeRepository.findAllBySessionIdAndSoptStoryIn(sessionId, soptStorys);

		var soptStoryDtos = soptStorys.stream()
			.map(soptStory -> toSoptStoryResponseDto(
					soptStory,
					likedSoptStorys.stream()
					.anyMatch(like -> like.getSoptStory().getId().equals(soptStory.getId()))
			))
			.toList();

		return new PaginateResponseDto<>(
				soptStoryDtos,
				totalCount,
			requestDto.getLimit(),
			requestDto.getPageNo()
		);
	}

	@Override
	@Transactional
	public LikeSoptStoryResponseDto like(Long id, String session) {
		SoptStoryEntity soptStory = soptStoryRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NotFoundSoptStory id: " + id));

		if (isLiked(id, session)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AlreadyLike");
		}

		SoptStoryLikeEntity soptStoryLike = SoptStoryLikeEntity.builder()
			.soptStory(soptStory)
			.sessionId(session)
			.build();

		soptStoryLikeRepository.save(soptStoryLike);
		soptStory.incrementLikeCount();

		return toLikeSoptStoryResponseDto(soptStoryLike);
	}

	private boolean isLiked(Long soptStoryId, String session) {
		return soptStoryLikeRepository.existsBySoptStoryIdAndSessionId(soptStoryId, session);
	}

	@Override
	@Transactional
	public LikeSoptStoryResponseDto unlike(Long id, String session) {
		SoptStoryEntity soptStory = soptStoryRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NotFoundSoptStory id: " + id));

		SoptStoryLikeEntity soptStoryLike = soptStoryLikeRepository.findBySoptStoryIdAndSessionId(id, session)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Like 하지 않은 상태입니다."));

		soptStory.decrementLikeCount();
		soptStoryLikeRepository.delete(soptStoryLike);

		return toLikeSoptStoryResponseDto(soptStoryLike);
	}

	@Override
	@Transactional
	public CreateSoptStoryResponseDto CreateSoptStory(CreateSoptStoryDto dto) {
		 if (soptStoryRepository.existsBySoptStoryUrl(dto.getLink())) {
		 	throw new BusinessLogicException("이미 등록된 솝티클입니다.");
		 }

		CreateScraperResponseDto scrapResult = scraperService.scrap(new ScrapArticleDto(dto.getLink()));

		SoptStoryEntity soptStory = SoptStoryEntity.builder()
				.thumbnailUrl(scrapResult.getThumbnailUrl())
				.title(scrapResult.getTitle())
				.description(scrapResult.getDescription())
				.soptStoryUrl(scrapResult.getArticleUrl())
				.build();

		soptStoryRepository.save(soptStory);

		return CreateSoptStoryResponseDto.builder()
			.thumbnailUrl(scrapResult.getThumbnailUrl())
			.title(scrapResult.getTitle())
			.description(scrapResult.getDescription())
			.soptStoryUrl(scrapResult.getArticleUrl())
			.build();

	}

	private SoptStoryResponseDto toSoptStoryResponseDto(SoptStoryEntity entity, boolean liked) {
		return SoptStoryResponseDto.builder()
			.id(entity.getId())
			.thumbnailUrl(entity.getThumbnailUrl())
			.title(entity.getTitle())
			.description(entity.getDescription())
			.url(entity.getSoptStoryUrl())
			.uploadedAt(entity.getCreatedAt())
			.likeCount(entity.getLikeCount())
			.build();
	}

	private LikeSoptStoryResponseDto toLikeSoptStoryResponseDto(SoptStoryLikeEntity entity) {
		return LikeSoptStoryResponseDto.builder()
			.id(entity.getId())
			.soptStoryId(entity.getSoptStory().getId())
			.sessionId(entity.getSessionId())
			.build();
	}
}
