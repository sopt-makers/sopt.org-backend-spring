package sopt.org.homepage.global.common.response;

/**
 * 공통 응답 형식.
 *
 * <p>통합 백엔드(sopt-makers-backend)의 {@code BaseResponse}와 같은 구조다. 프론트엔드가 통합 백엔드로 트래픽을
 * 옮길 때 응답 파싱 코드를 바꾸지 않도록 레거시도 같은 형식으로 응답한다.
 */
public record BaseResponse<T>(boolean success, String message, T data) {

    public static final String DEFAULT_SUCCESS_MESSAGE = "요청에 성공했습니다";

    public static <T> BaseResponse<T> ofSuccess(T data) {
        return new BaseResponse<>(true, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static BaseResponse<Void> ofFailure(String message) {
        return new BaseResponse<>(false, message, null);
    }
}
