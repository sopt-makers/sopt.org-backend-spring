package sopt.org.homepage.generation.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MainButton Value Object
 *
 * 메인 페이지 버튼 설정
 * - text: 버튼 텍스트
 * - keyColor: 주요 컬러
 * - subColor: 보조 컬러
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MainButton {

    @Column(name = "\"mainButtonText\"", nullable = false, length = 50)
    private String text;

    @Column(name = "\"mainButtonKeyColor\"", nullable = false, length = 7)
    private String keyColor;

    @Column(name = "\"mainButtonSubColor\"", nullable = false, length = 7)
    private String subColor;

    @Builder
    public MainButton(String text, String keyColor, String subColor) {
        validateText(text);
        validateHexColor(keyColor, "keyColor");
        validateHexColor(subColor, "subColor");

        this.text = text;
        this.keyColor = keyColor;
        this.subColor = subColor;
    }

    private void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Button text must not be blank");
        }
        if (text.length() > 50) {
            throw new IllegalArgumentException("Button text must be less than 50 characters");
        }
    }

    private void validateHexColor(String color, String fieldName) {
        if (color == null || !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException(
                    String.format("%s must be a valid hex color code (e.g., #FF0000)", fieldName)
            );
        }
    }
}