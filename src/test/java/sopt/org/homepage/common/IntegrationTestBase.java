package sopt.org.homepage.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * TestContainer 기반 통합 테스트 Base 클래스
 * - 실제 PostgreSQL 컨테이너 사용
 * - 고전파 스타일 테스트 (Mock 최소화)
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:14.17-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgres.start();
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // AuthConfig를 위한 프로파일 설정
        registry.add("spring.profiles.active", () -> "test");
    }
}