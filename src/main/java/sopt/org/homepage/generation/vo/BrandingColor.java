package sopt.org.homepage.generation.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BrandingColor Value Object
 * <p>
 * 기수별 브랜딩 컬러 설정 - main: 메인 컬러 - sub: 서브 컬러 (기존 low) - point: 포인트 컬러 (기존 high) - background: 배경 컬러 (기존 point)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandingColor {

    @Column(name = "\"brandingColorMain\"", nullable = false, length = 7)
    private String main;  // #FF0000

    @Column(name = "\"brandingColorSub\"", nullable = false, length = 7)
    private String sub;   // #CC0000 (기존 low)

    @Column(name = "\"brandingColorPoint\"", nullable = false, length = 7)
    private String point; // #FF3333 (기존 high)

    @Column(name = "\"brandingColorBackground\"", nullable = false, length = 7)
    private String background;  // #FF9999 (기존 point)

    @Builder
    public BrandingColor(String main, String sub, String point, String background) {
        validateHexColor(main, "main");
        validateHexColor(sub, "sub");
        validateHexColor(point, "point");
        validateHexColor(background, "background");

        this.main = main;
        this.sub = sub;
        this.point = point;
        this.background = background;
    }

    /**
     * Hex 컬러 코드 검증
     */
    private void validateHexColor(String color, String fieldName) {
        if (color == null || !color.matches("^[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException(
                    String.format("%s must be a valid hex color code (e.g., #FF0000)", fieldName)
            );
        }
    }

    /**
     * 레거시 필드명 매핑을 위한 헬퍼 메서드
     */
    public String getLow() {
        return this.sub;
    }

    public String getHigh() {
        return this.point;
    }
}
