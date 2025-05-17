package sopt.org.homepage.soptstory.service;

import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.soptstory.dto.request.CreateSoptStoryDto;
import sopt.org.homepage.soptstory.dto.request.GetSoptStoryListRequestDto;
import sopt.org.homepage.soptstory.dto.response.CreateSoptStoryResponseDto;
import sopt.org.homepage.soptstory.dto.response.LikeSoptStoryResponseDto;
import sopt.org.homepage.soptstory.dto.response.SoptStoryResponseDto;

public interface SoptStoryService {
    /**
     * 솝트 스토리 목록을 페이지네이션하여 조회합니다.
     *
     * @param requestDto 조회 조건 및 페이지네이션 정보
     * @param sessionId 사용자 세션 ID
     * @return 페이지네이션된 솝트스토리 목록
     */
    PaginateResponseDto<SoptStoryResponseDto> paginateSoptStorys(GetSoptStoryListRequestDto requestDto, String sessionId);

    /**
     * 솝트스토리에 좋아요를 추가합니다.
     *
     * @param id 솝트스토리 ID
     * @param session 사용자 세션 ID
     * @return 좋아요 정보
     */
    LikeSoptStoryResponseDto like(Long id, String session);

    /**
     * 솝트 스토리의 좋아요를 취소합니다.
     *
     * @param id 솝트스토리 ID
     * @param session 사용자 세션 ID
     * @return 좋아요 정보
     */
    LikeSoptStoryResponseDto unlike(Long id, String session);

    /**
     * 새로운 솝트스토리를을 생성합니다.
     *
     * @param dto 생성할 솝트스토리 정보
     * @return 생성된 솝트스토리 정보
     */
    CreateSoptStoryResponseDto createSoptStory(CreateSoptStoryDto dto);
}