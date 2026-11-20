package sopt.org.homepage.news;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

public enum NewsType {
    BLOG, INSTAGRAM, OTHER;

    private static final String INSTAGRAM_DOMAIN = "instagram.com";
    private static final Set<String> BLOG_DOMAINS = Set.of(
            "blog.naver.com",
            "tistory.com",
            "velog.io",
            "brunch.co.kr",
            "medium.com",
            "blog.sopt.org"
    );

    /** 기존 클라이언트가 type을 생략하면 알려진 호스트로 출처를 판단한다. */
    public static NewsType fromLink(String link) {
        try {
            String host = URI.create(link).getHost();
            if (host == null) return OTHER;
            host = host.toLowerCase(Locale.ROOT);
            if (matches(host, INSTAGRAM_DOMAIN)) return INSTAGRAM;
            for (String domain : BLOG_DOMAINS) {
                if (matches(host, domain)) return BLOG;
            }
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return OTHER;
        }
        return OTHER;
    }

    private static boolean matches(String host, String domain) {
        return host.equals(domain) || host.endsWith("." + domain);
    }
}
