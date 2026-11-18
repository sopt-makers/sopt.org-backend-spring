package sopt.org.homepage.global.common.response;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sopt.org.homepage.common.IntegrationTestBase;

/**
 * 공통 응답 래핑 통합 테스트
 *
 * 검증 범위:
 * - 실제 컨트롤러의 성공·실패 응답이 공통 응답 형식으로 나감
 * - 헬스체크, springdoc 문서는 감싸지 않음
 */
@DisplayName("공통 응답 래핑 통합 테스트")
class BaseResponseIntegrationTest extends IntegrationTestBase {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("✅ 일반 API 성공 응답은 공통 응답으로 감쌈")
    void wrapsControllerResponse() throws Exception {
        mvc.perform(get("/visitor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(BaseResponse.DEFAULT_SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("❌ GlobalExceptionHandler 에러 응답은 success=false로 감쌈")
    void wrapsExceptionHandlerResponse() throws Exception {
        mvc.perform(get("/projects/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 파라미터 값입니다: projectId=abc"));
    }

    @Test
    @DisplayName("✅ 헬스체크는 감싸지 않음")
    void skipsHealthCheck() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("HealthCheck Complete"));
    }

    @Test
    @DisplayName("✅ springdoc API 문서는 감싸지 않음")
    void skipsApiDocs() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.success").doesNotExist());
    }
}
