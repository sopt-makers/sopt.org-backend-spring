package sopt.org.homepage.global.common.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 컨트롤러와 예외 핸들러의 응답 본문을 {@link BaseResponse}로 감싼다.
 *
 * <ul>
 *   <li>HTTP 상태가 400 미만이면 {@code {success: true, message: "요청에 성공했습니다", data: 원래 본문}}</li>
 *   <li>400 이상이면 {@code {success: false, message: 원래 본문 문자열, data: null}}.
 *       {@code GlobalExceptionHandler}가 돌려주는 에러 문자열이 그대로 message가 된다.</li>
 *   <li>HTTP 상태 코드는 바꾸지 않는다.</li>
 * </ul>
 *
 * <p>감싸지 않는 경우
 * <ul>
 *   <li>요청을 처리한 컨트롤러나 메서드에 {@link RawResponse}가 붙은 경우(예외 응답 포함)</li>
 *   <li>이 애플리케이션 패키지 밖의 컨트롤러(springdoc, actuator, Spring 기본 {@code /error})</li>
 *   <li>JSON·문자열이 아닌 본문(파일, byte[] 등)</li>
 * </ul>
 */
@RestControllerAdvice(basePackages = "sopt.org.homepage")
@RequiredArgsConstructor
public class BaseResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType)
                || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof BaseResponse<?>) {
            return body;
        }
        if (!(request instanceof ServletServerHttpRequest servletRequest)
                || !(response instanceof ServletServerHttpResponse servletResponse)) {
            return body;
        }
        if (isRawResponse(servletRequest.getServletRequest())) {
            return body;
        }

        BaseResponse<?> wrapped = wrap(body, servletResponse.getServletResponse().getStatus());

        // 반환 타입이 String이면 StringHttpMessageConverter가 이미 선택된 상태라 객체를 넘기면 ClassCastException이 난다.
        // 직접 JSON 문자열로 만들어 넘기고 Content-Type도 JSON으로 바꾼다.
        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return toJson(wrapped);
        }
        return wrapped;
    }

    private BaseResponse<?> wrap(Object body, int status) {
        if (status < HttpStatus.BAD_REQUEST.value()) {
            return BaseResponse.ofSuccess(body);
        }
        if (body instanceof String message) {
            return BaseResponse.ofFailure(message);
        }
        HttpStatus httpStatus = HttpStatus.resolve(status);
        String message = httpStatus != null ? httpStatus.getReasonPhrase() : "Error";
        return new BaseResponse<>(false, message, body);
    }

    private boolean isRawResponse(HttpServletRequest request) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        return handlerMethod.hasMethodAnnotation(RawResponse.class)
                || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), RawResponse.class);
    }

    private String toJson(BaseResponse<?> wrapped) {
        try {
            return objectMapper.writeValueAsString(wrapped);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("응답을 JSON으로 변환하지 못했습니다", e);
        }
    }
}
