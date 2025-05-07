package sopt.org.homepage.soptstory.service;

import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.soptstory.dto.request.CreateSopticleDto;
import sopt.org.homepage.soptstory.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.soptstory.dto.response.CreateSopticleResponseDto;
import sopt.org.homepage.soptstory.dto.response.LikeSopticleResponseDto;
import sopt.org.homepage.soptstory.dto.response.SopticleResponseDto;

public interface SopticleService {
    /**
     * 솝티클 목록을 페이지네이션하여 조회합니다.
     *
     * @param requestDto 조회 조건 및 페이지네이션 정보
     * @param sessionId 사용자 세션 ID
     * @return 페이지네이션된 솝티클 목록
     */
    PaginateResponseDto<SopticleResponseDto> paginateSopticles(GetSopticleListRequestDto requestDto, String sessionId);

    /**
     * 솝티클에 좋아요를 추가합니다.
     *
     * @param id 솝티클 ID
     * @param session 사용자 세션 ID
     * @return 좋아요 정보
     */
    LikeSopticleResponseDto like(Long id, String session);

    /**
     * 솝티클의 좋아요를 취소합니다.
     *
     * @param id 솝티클 ID
     * @param session 사용자 세션 ID
     * @return 좋아요 정보
     */
    LikeSopticleResponseDto unlike(Long id, String session);

    /**
     * 새로운 솝티클을 생성합니다.
     *
     * @param dto 생성할 솝티클 정보
     * @return 생성된 솝티클 정보
     */
    CreateSopticleResponseDto createSopticle(CreateSopticleDto dto);
}