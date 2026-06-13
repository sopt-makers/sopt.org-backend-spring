package sopt.org.homepage.infrastructure.external.scrap.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sopt.org.homepage.infrastructure.external.scrap.LinkCrawler;
import sopt.org.homepage.infrastructure.external.scrap.LinkSource;
import sopt.org.homepage.infrastructure.external.scrap.dto.CreateScraperResponseDto;

@Slf4j
@Component
public class MediumLinkCrawler implements LinkCrawler {

    private static final String SOPT_MAKERS_RSS_URL = "https://medium.com/feed/sopt-makers";
    private static final int TIMEOUT_MILLISECONDS = 10000;
    private static final Pattern ARTICLE_ID_PATTERN = Pattern.compile("([0-9a-f]{12})/?(?:\\?.*)?$");

    @Override
    public LinkSource supportSource() {
        return LinkSource.MEDIUM;
    }

    @Override
    public CreateScraperResponseDto crawl(String link) throws IOException {
        String articleId = extractArticleId(link);
        Document rss = fetchRss();

        Element item = rss.select("item").stream()
                .filter(i -> {
                    Element guid = i.selectFirst("guid");
                    return guid != null && guid.text().endsWith(articleId);
                })
                .findFirst()
                .orElseThrow(() -> new IOException("Article not found in RSS feed: " + articleId));

        String title = getElementText(item, "title");
        String contentEncoded = getElementText(item, "content|encoded");

        Document contentDoc = Jsoup.parse(contentEncoded);
        String thumbnailUrl = extractThumbnail(contentDoc);
        String description = extractDescription(contentDoc);

        return new CreateScraperResponseDto(thumbnailUrl, title, description, link, "미디엄");
    }

    private String extractArticleId(String link) throws IOException {
        if (link == null || link.isBlank()) {
            throw new IOException("Link cannot be null or empty");
        }
        Matcher matcher = ARTICLE_ID_PATTERN.matcher(link.trim());
        if (!matcher.find()) {
            throw new IOException("Cannot extract article ID from URL: " + link);
        }
        return matcher.group(1);
    }

    private Document fetchRss() throws IOException {
        URL url = new URL(SOPT_MAKERS_RSS_URL);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
        connection.setReadTimeout(TIMEOUT_MILLISECONDS);
        try (InputStream inputStream = connection.getInputStream()) {
            return Jsoup.parse(inputStream, "UTF-8", SOPT_MAKERS_RSS_URL, Parser.xmlParser());
        }
    }

    private String getElementText(Element parent, String selector) {
        Element element = parent.selectFirst(selector);
        return element != null ? element.text() : "";
    }

    private String extractThumbnail(Document contentDoc) {
        Element img = contentDoc.selectFirst("figure img");
        if (img == null) {
            img = contentDoc.selectFirst("img");
        }
        return img != null ? img.attr("src") : "";
    }

    private String extractDescription(Document contentDoc) {
        Element paragraph = contentDoc.selectFirst("p");
        return paragraph != null ? paragraph.text() : "";
    }
}
