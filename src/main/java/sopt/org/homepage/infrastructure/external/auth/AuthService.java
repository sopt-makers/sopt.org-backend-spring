package sopt.org.homepage.infrastructure.external.auth;

public interface AuthService {
    /**
     * 특정 기수의 사용자 수를 조회합니다.
     *
     * @param generation 조회할 기수
     * @return 해당 기수의 사용자 수
     */
    int getUserCountByGeneration(Integer generation);
}
