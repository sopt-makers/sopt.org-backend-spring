package sopt.org.homepage.global.common.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.global.common.filter.JwtExceptionFilter;
import sopt.org.homepage.global.exception.GlobalExceptionHandler;
import sopt.org.homepage.global.exception.TokenException;

/**
 * 공통 응답 래핑 테스트
 *
 * 검증 범위:
 * - 성공 응답은 {success: true, message, data}로 감싸고 HTTP 상태는 유지
 * - String 반환도 JSON 공통 응답으로 감쌈
 * - GlobalExceptionHandler의 에러 문자열은 {success: false, message}로 감쌈
 * - @RawResponse 컨트롤러는 성공·실패 모두 감싸지 않음
 * - JWT 필터 에러도 공통 응답 형식
 */
@DisplayName("BaseResponseAdvice 테스트")
class BaseResponseAdviceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new WrappedController(), new RawController())
                .setControllerAdvice(new GlobalExceptionHandler(), new BaseResponseAdvice(objectMapper))
                .build();
    }

    @Test
    @DisplayName("✅ DTO 응답은 data에 담기고 HTTP 상태는 그대로")
    void wrapsDto() throws Exception {
        mvc.perform(post("/test/dto"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(BaseResponse.DEFAULT_SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data.name").value("sopt"))
                .andExpect(jsonPath("$.data.tags[0]").value("a"));
    }

    @Test
    @DisplayName("✅ String 응답도 JSON 공통 응답으로 감쌈")
    void wrapsString() throws Exception {
        mvc.perform(get("/test/string"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("후기 등록 완료"));
    }

    @Test
    @DisplayName("✅ 이미 BaseResponse면 다시 감싸지 않음")
    void doesNotDoubleWrap() throws Exception {
        mvc.perform(get("/test/base"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @DisplayName("❌ 예외 응답은 success=false, 기존 에러 문자열이 message, 상태 코드는 그대로")
    void wrapsError() throws Exception {
        mvc.perform(get("/test/error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 요청"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("✅ @RawResponse 컨트롤러는 성공 응답을 감싸지 않음")
    void skipsRawSuccess() throws Exception {
        mvc.perform(get("/raw/string"))
                .andExpect(status().isOk())
                .andExpect(content().string("HealthCheck Complete"));
        mvc.perform(get("/raw/dto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("sopt"))
                .andExpect(jsonPath("$.success").doesNotExist());
    }

    @Test
    @DisplayName("❌ @RawResponse 컨트롤러는 예외 응답도 기존 문자열 그대로")
    void skipsRawError() throws Exception {
        mvc.perform(get("/raw/error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("invalid request"));
    }

    @Test
    @DisplayName("❌ JWT 필터 에러도 공통 응답 형식")
    void wrapsJwtFilterError() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            throw new TokenException("만료된 토큰");
        };

        new JwtExceptionFilter().doFilter(new MockHttpServletRequest(), response, chain);

        var body = objectMapper.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(body.get("success").asBoolean()).isFalse();
        assertThat(body.get("message").asText())
                .isEqualTo("[TokenException] : 만료된 토큰");
        assertThat(body.get("data").isNull()).isTrue();
    }

    record SampleDto(String name, List<String> tags) {
    }

    @RestController
    @RequestMapping("/test")
    static class WrappedController {

        @PostMapping("/dto")
        ResponseEntity<SampleDto> dto() {
            return ResponseEntity.status(HttpStatus.CREATED).body(new SampleDto("sopt", List.of("a")));
        }

        @GetMapping("/string")
        ResponseEntity<String> string() {
            return ResponseEntity.ok("후기 등록 완료");
        }

        @GetMapping("/base")
        ResponseEntity<BaseResponse<Integer>> base() {
            return ResponseEntity.ok(BaseResponse.ofSuccess(1));
        }

        @GetMapping("/error")
        ResponseEntity<String> error() {
            throw new IllegalArgumentException("잘못된 요청");
        }
    }

    @RawResponse
    @RestController
    @RequestMapping("/raw")
    static class RawController {

        @GetMapping("/string")
        ResponseEntity<String> string() {
            return ResponseEntity.ok("HealthCheck Complete");
        }

        @GetMapping("/dto")
        ResponseEntity<SampleDto> dto() {
            return ResponseEntity.ok(new SampleDto("sopt", List.of()));
        }

        @GetMapping("/error")
        ResponseEntity<String> error() {
            // text/plain 응답은 MockMvc가 ISO-8859-1로 읽어서 ASCII 메시지로 검증한다
            throw new IllegalArgumentException("invalid request");
        }
    }
}
