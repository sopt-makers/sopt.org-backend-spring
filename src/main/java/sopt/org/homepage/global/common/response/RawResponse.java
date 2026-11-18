package sopt.org.homepage.global.common.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이 컨트롤러(또는 메서드)의 응답은 {@link BaseResponse}로 감싸지 않는다.
 *
 * <p>헬스체크처럼 본문 형식이 정해져 있거나, 다른 팀 서버가 호출해 응답 형식을 바꿀 수 없는 API에 붙인다. 예외 응답도
 * 감싸지 않고 기존 형식 그대로 나간다.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RawResponse {
}
