package sopt.org.homepage.application.visitor.service;

import jakarta.servlet.http.HttpServletRequest;
import sopt.org.homepage.application.visitor.dto.GetTodayVisitorResponseDto;
import sopt.org.homepage.application.visitor.dto.VisitorCountUpResponseDto;

public interface VisitorService {
    /**
     * 방문자 수를 증가시킵니다.
     *
     * @param request HTTP 요청 정보
     * @return 방문자 수 증가 결과
     */
    VisitorCountUpResponseDto visitorCountUp(HttpServletRequest request);

    /**
     * 오늘의 방문자 수를 조회합니다.
     *
     * @return 오늘의 방문자 수 정보
     */
    GetTodayVisitorResponseDto getTodayVisitor();

    /**
     * 방문자 수를 초기화합니다. 매일 자정에 실행됩니다.
     */
    void visitorReset();
}
