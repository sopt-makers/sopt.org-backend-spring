package sopt.org.homepage.news.instagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsRepository;
import sopt.org.homepage.news.NewsService;
import sopt.org.homepage.news.dto.BulkCreateNewsCommand;

class InstagramNewsFilterTest extends InstagramNewsIntegrationTestBase {
    @Test
    @DisplayName("파트장 예능·파트 소개는 노출하고 미디어팀 인사말은 제외 사유로 보지 않음")
    void keepsPartContent() {
        for (String title : List.of("파트장 영통 팬사인회", "파트 소개", "파트티 자랑대회")) {
            assertThat(filter.shouldPublish("[" + title + "]\n안녕하세요, 미디어팀 나우유씨미입니다"))
                    .as(title).isTrue();
        }
    }

    @Test
    @DisplayName("본문의 참가 신청 언급만으로 활동 스케치를 제외하지 않음")
    void noticeChecksTitleOnly() {
        assertThat(filter.shouldPublish("[APPJAM 현장 스케치]\n참가 신청부터 마무리까지 함께한 이야기")).isTrue();
        assertThat(filter.shouldPublish("[APPJAM 참가 신청 안내]\n현장 스케치를 준비합니다")).isFalse();
    }

    @Test
    @DisplayName("photo·hot 등의 일부인 ot는 주제 키워드로 인정하지 않음")
    void otMatchesOnlyOutsideLatinWords() {
        for (String word : List.of("photo", "hot", "spot", "not")) {
            assertThat(filter.shouldPublish("[신규 서비스 소개]\n" + word)).as(word).isFalse();
        }
        assertThat(filter.shouldPublish("[OT 영상]\n함께한 첫 만남")).isTrue();
        assertThat(filter.shouldPublish("[첫 만남 현장 스케치]\nOT에서 만났어요")).isTrue();
        assertThat(filter.shouldPublish("[현장 스케치]\n세미나에서 만났어요")).isTrue();
    }

    @Test
    @DisplayName("🔍 제목 경계와 준비물 안내를 처리")
    void handlesTitleBoundaries() {
        assertThat(filter.title("[솝트룩북] 34기 OB편")).isEqualTo("솝트룩북 34기 OB편");
        assertThat(filter.title("[A] 본문 [B]")).isEqualTo("A 본문 [B]");
        assertThat(filter.shouldPublish("[OT 준비물 안내]")).isTrue();
    }

}
