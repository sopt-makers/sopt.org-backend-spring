package sopt.org.homepage.news;

import java.net.URI;
import java.util.Locale;

public enum NewsType {
    BLOG, INSTAGRAM, OTHER;

    /** 기존 클라이언트가 type을 생략하면 알려진 호스트로 출처를 판단한다. */
    public static NewsType fromLink(String link) {
        try {
            String host = URI.create(link).getHost();
            if (host == null) return OTHER;
            host = host.toLowerCase(Locale.ROOT);
            if (matches(host, "instagram.com")) return INSTAGRAM;
            if (matches(host, "blog.naver.com") || matches(host, "tistory.com")
                    || matches(host, "velog.io") || matches(host, "brunch.co.kr")
                    || matches(host, "medium.com") || matches(host, "blog.sopt.org")) return BLOG;
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return OTHER;
        }
        return OTHER;
    }

    private static boolean matches(String host, String domain) {
        return host.equals(domain) || host.endsWith("." + domain);
    }
}
