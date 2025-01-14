package sopt.org.homepage.aboutsopt;

import sopt.org.homepage.aboutsopt.dto.GetAboutSoptResponseDto;

public interface AboutSoptService {
    /**
     * 특정 기수의 AboutSopt 정보를 조회.
     *
     * @param generation 조회할 기수. null인 경우 최근 기수 정보를 조회.
     * @return AboutSopt 정보 및 활동 기록이 포함된 응답 DTO
     */
    GetAboutSoptResponseDto getAboutSopt(Integer generation);

    /**
     * 특정 기수의 스터디 수를 조회.
     *
     * @param generation 조회할 기수
     * @return 해당 기수의 스터디 수
     */
    Integer getStudyCount(Integer generation);
}