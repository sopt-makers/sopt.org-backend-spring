package sopt.org.homepage.news.instagram;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/** 캡션 규칙으로만 분류한다. 제외 규칙을 노출 규칙보다 먼저 적용한다. */
@Component
public class InstagramNewsFilter {
    private static final List<String> INTERNAL_NOTICES = List.of(
            "명예의전당", "참가신청", "참여신청", "신청마감", "참여안내", "참가안내",
            "장비콘테스트안내", "노꾸자랑대회");
    private static final List<String> SERIES = List.of(
            "sopt를만나다", "솝트를만나다", "변절자들", "episopt", "파트장영통팬사인회",
            "파트소개", "파트티자랑대회", "밸런스게임", "솝든벨", "sopti", "soptlist",
            "솝일리룩", "sopt룩북", "솝트룩북", "sopt또간집", "솝트또간집");
    private static final List<String> TOPICS = List.of(
            "appjam", "앱잼", "데모데이", "솝커톤", "합동세미나", "스터디", "번개", "컨퍼런스", "세미나", "ot", "오티");
    private static final List<String> CONTENT_TYPES = List.of(
            "스케치", "꿀팁", "준비물", "후기", "소개", "톺아보기", "일상", "현장", "영상", "끝나지않은몰입");

    public boolean shouldPublish(String caption) {
        String title = title(caption);
        String heading = normalize(title);
        String body = normalize(caption == null ? "" : caption);
        if (title.isBlank()) return false;
        // 미디어팀 인사말은 거의 모든 캡션에 있으므로 팀 소개 여부는 제목으로만 판단한다.
        if ((heading.contains("미디어팀") || heading.contains("나우유씨미"))
                && containsAny(heading, List.of("소개", "모집", "브랜딩"))) {
            return false;
        }
        if (heading.contains("임원진") && heading.contains("소개")) {
            return false;
        }
        if (heading.contains("파트장을소개")) {
            return false;
        }
        if (containsAny(body, INTERNAL_NOTICES) || containsAny(heading, List.of("모집", "공지", "신청", "안내"))) {
            return false;
        }
        if (containsAny(heading, SERIES)) return true;
        if (containsAny(body, TOPICS) && containsAny(heading, CONTENT_TYPES)) {
            return true;
        }
        if (containsAny(heading, List.of("면접", "지원"))
                && containsAny(heading, List.of("팁", "꿀팁", "면접관앞에앉아있었다"))) {
            return true;
        }
        if (heading.contains("협업고수")) return true;
        return false;
    }

    /** 첫 줄의 바깥 대괄호만 제거하고 이모지와 내부 문구는 유지한다. */
    public String title(String caption) {
        if (caption == null) return "";
        String firstLine = caption.strip().lines().findFirst().orElse("").strip();
        if (firstLine.startsWith("[") && firstLine.endsWith("]")) {
            return firstLine.substring(1, firstLine.length() - 1).strip();
        }
        return firstLine;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]", "");
    }

    private boolean containsAny(String text, List<String> terms) {
        return terms.stream().anyMatch(text::contains);
    }

}
