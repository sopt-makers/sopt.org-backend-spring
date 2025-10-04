package sopt.org.homepage.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.command.NotificationCommandRepository;
import sopt.org.homepage.common.IntegrationTestBase;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NotificationV2Controller 통합 테스트
 * - MockMvc로 HTTP 요청/응답 검증
 * - TestContainer 기반 실제 DB 연동
 * - Spring Validation 동작 확인
 *
 * Note: GlobalExceptionHandler가 평문(String) 응답을 반환하므로
 *       content().string()으로 검증 (JSON이 아님)
 */
@DisplayName("NotificationV2Controller 통합 테스트")
@AutoConfigureMockMvc
class NotificationV2ControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationCommandRepository commandRepository;

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /notification/v2/register - 유효한 요청으로 알림 신청 성공")
    void register_WithValidRequest_Success() throws Exception {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                35
        );

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())  // 201 Created
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("test@sopt.org"))
                .andExpect(jsonPath("$.generation").value(35))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("POST /notification/v2/register - 중복된 이메일+기수로 신청 시 400 에러")
    void register_WithDuplicateEmailAndGeneration_BadRequest() throws Exception {
        // given: 이미 존재하는 데이터
        Notification existing = Notification.create(
                new Email("test@sopt.org"),
                new Generation(35)
        );
        commandRepository.save(existing);

        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                35
        );

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("이미 등록된")));
    }

    @Test
    @DisplayName("POST /notification/v2/register - 이메일 누락 시 400 에러 (Validation)")
    void register_WithNullEmail_ValidationError() throws Exception {
        // given: email이 null인 요청
        String requestJson = """
                {
                    "generation": 35
                }
                """;

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /notification/v2/register - 잘못된 이메일 형식으로 400 에러 (Validation)")
    void register_WithInvalidEmailFormat_ValidationError() throws Exception {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "invalid-email",  // @ 없는 잘못된 형식
                35
        );

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /notification/v2/register - 기수가 음수인 경우 400 에러 (Validation)")
    void register_WithNegativeGeneration_ValidationError() throws Exception {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                -1  // 음수
        );

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /notification/v2/register - 기수가 0인 경우 400 에러 (Validation)")
    void register_WithZeroGeneration_ValidationError() throws Exception {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                0  // 0은 @Positive에 위반
        );

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /notification/v2/register - 기수가 범위를 초과하는 경우 400 에러")
    void register_WithGenerationOverLimit_BadRequest() throws Exception {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                101  // 기수 범위 초과 (1~100)
        );

        // when & then
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("100기 이하")));
    }

    @Test
    @DisplayName("GET /notification/v2/list - 특정 기수의 알림 목록 조회 성공")
    void getNotificationList_WithValidGeneration_Success() throws Exception {
        // given: 35기 데이터 2개, 36기 데이터 1개 준비
        Notification notification1 = Notification.create(
                new Email("test1@sopt.org"),
                new Generation(35)
        );
        Notification notification2 = Notification.create(
                new Email("test2@sopt.org"),
                new Generation(35)
        );
        Notification notification3 = Notification.create(
                new Email("test3@sopt.org"),
                new Generation(36)
        );
        commandRepository.save(notification1);
        commandRepository.save(notification2);
        commandRepository.save(notification3);

        // when & then: 35기만 조회
        mockMvc.perform(get("/notification/v2/list")
                        .param("generation", "35"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generation").value(35))
                .andExpect(jsonPath("$.emailList", hasSize(2)))
                .andExpect(jsonPath("$.emailList", containsInAnyOrder(
                        "test1@sopt.org",
                        "test2@sopt.org"
                )));
    }

    @Test
    @DisplayName("GET /notification/v2/list - 알림이 없는 기수 조회 시 빈 배열 반환")
    void getNotificationList_NoNotifications_EmptyList() throws Exception {
        // when & then: 데이터가 없는 기수 조회
        mockMvc.perform(get("/notification/v2/list")
                        .param("generation", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generation").value(99))
                .andExpect(jsonPath("$.emailList", hasSize(0)));
    }

    @Test
    @DisplayName("GET /notification/v2/list - 기수 파라미터 누락 시 400 에러")
    void getNotificationList_WithoutGenerationParam_BadRequest() throws Exception {
        // when & then: generation 파라미터 없이 요청
        mockMvc.perform(get("/notification/v2/list"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /notification/v2/list - 음수 기수로 조회 시 400 에러")
    void getNotificationList_WithNegativeGeneration_BadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/notification/v2/list")
                        .param("generation", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /notification/v2/list - 기수 범위 초과로 조회 시 400 에러")
    void getNotificationList_WithGenerationOverLimit_BadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/notification/v2/list")
                        .param("generation", "999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("100기 이하")));
    }

    @Test
    @DisplayName("같은 이메일, 다른 기수로 여러 번 신청 가능")
    void register_SameEmailDifferentGeneration_Success() throws Exception {
        // given: 35기 신청
        RegisterNotificationRequest request1 = new RegisterNotificationRequest(
                "test@sopt.org",
                35
        );
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // when & then: 같은 이메일로 36기 신청 - 성공해야 함
        RegisterNotificationRequest request2 = new RegisterNotificationRequest(
                "test@sopt.org",
                36
        );
        mockMvc.perform(post("/notification/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andDo(print())
                .andExpect(status().isCreated())  // 201 Created
                .andExpect(jsonPath("$.email").value("test@sopt.org"))
                .andExpect(jsonPath("$.generation").value(36));
    }
}