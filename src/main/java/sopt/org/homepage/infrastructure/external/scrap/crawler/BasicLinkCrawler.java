package sopt.org.homepage.infrastructure.external.scrap.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import sopt.org.homepage.infrastructure.external.scrap.LinkCrawler;
import sopt.org.homepage.infrastructure.external.scrap.LinkSource;
import sopt.org.homepage.infrastructure.external.scrap.dto.CreateScraperResponseDto;

@Component
public class BasicLinkCrawler implements LinkCrawler {

    private static final int TIMEOUT_MILLISECONDS = 10000;
    private static final String EMPTY_STRING = "";

    @Override
    public LinkSource supportSource() {
        return LinkSource.BASIC;
    }

    @Override
    public CreateScraperResponseDto crawl(String link) throws IOException {
        Document target = scrap(link);
        String title = fetchMeta(target, "title");
        String url = fetchMeta(target, "url");
        String image = fetchMeta(target, "image");
        String description = fetchMeta(target, "description");
        String platform = determinePlatform(link);
        return new CreateScraperResponseDto(image, title, description, url, platform);
    }

    private Document scrap(String link) throws IOException {
        URL url = new URL(link);

        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
        connection.setReadTimeout(TIMEOUT_MILLISECONDS);

        InputStream inputStream = connection.getInputStream();

        Document doc = Jsoup.parse(inputStream, "UTF-8", link);
        inputStream.close();

        return doc;
    }

    private String fetchMeta(Document target, String tag) {
        Element element = target.selectFirst("meta[property=og:%s]".formatted(tag));
        if (element == null) {
            return EMPTY_STRING;
        }
        return element.attr("content");
    }

    private String determinePlatform(String link) {
        if (link.contains("tistory.com")) {
            return "티스토리";
        } else if (link.contains("github.com") || link.contains("github.io")) {
            return "깃헙";
        } else if (link.contains("medium.com")) {
            return "미디엄";
        } else if (link.contains("notion.so") || link.contains("notion.site")) {
            return "노션";
        } else {
            return "기타";
        }
    }

}
