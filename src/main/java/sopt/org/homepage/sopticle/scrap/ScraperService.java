package sopt.org.homepage.sopticle.scrap;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import sopt.org.homepage.sopticle.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.sopticle.scrap.dto.ScrapArticleDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public CreateScraperResponseDto scrap(ScrapArticleDto dto) {
        try {
            if (isNaverBlog(dto.getArticleUrl())) {
                return scrapeNaverBlog(dto.getArticleUrl());
            }
            return scrapeGeneral(dto.getArticleUrl());
        } catch (IOException e) {
            log.error("Scraping failed for URL: {}", dto.getArticleUrl(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "스크래핑에 실패했습니다");
        }
    }

    private CreateScraperResponseDto scrapeGeneral(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = getMetaTag(doc, "og:title");
        String description = getMetaTag(doc, "og:description");
        String thumbnailUrl = getMetaTag(doc, "og:image");

        return CreateScraperResponseDto.builder()
                .thumbnailUrl(thumbnailUrl)
                .title(title)
                .description(description)
                .articleUrl(url)
                .build();
    }

    private CreateScraperResponseDto scrapeNaverBlog(String url) throws IOException {
        WebDriver driver = setupChromeDriver();        // Selenium 설정
        try {
            driver.get(url);

            // iframe 대기 및 전환
            WebElement iframe = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("mainFrame")));
            driver.switchTo().frame(iframe);

            // 컨텐츠 추출
            String title = driver.findElement(By.cssSelector("span.se-fs-.se-ff-"))
                    .getText();
            String description = driver.findElement(By.cssSelector("div.se-main-container p"))
                    .getText();
            description = description.substring(0, Math.min(300, description.length()));

            // 이미지 URL 추출
            List<WebElement> images = driver.findElements(By.cssSelector("div.se-main-container img"));
            String thumbnailUrl = images.stream()
                    .map(img -> img.getAttribute("src"))
                    .filter(src -> src.contains("postfiles.pstatic.net"))
                    .map(this::processNaverImageUrl)
                    .findFirst()
                    .orElse(images.isEmpty() ? null : images.get(0).getAttribute("src"));

            return CreateScraperResponseDto.builder()
                    .thumbnailUrl(thumbnailUrl)
                    .title(title.isEmpty() ? generateTitle(description) : title)
                    .description(description)
                    .articleUrl(url)
                    .build();

        } finally {
            driver.quit();
        }
    }

    private String getMetaTag(Document doc, String property) {
        String content = doc.select("meta[property=" + property + "]").attr("content");
        if (content.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "페이지 정책에 의해 " + property + "를 가져올 수 없습니다.");
        }
        return content;
    }

    private boolean isNaverBlog(String url) {
        return url.contains("blog.naver.com");
    }

    private String generateTitle(String description) {
        return description.length() >= 150 ?
                description.substring(0, 30) :
                description;
    }

    private String processNaverImageUrl(String url) {
        return url.contains("w80_blur") ?
                url.replace("w80_blur", "w966") :
                url;
    }

    private WebDriver setupChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        if ("prod".equals(activeProfile)) {
            options.setBinary("/usr/bin/chromium-browser");
        }

        return new ChromeDriver(options);
    }
}
