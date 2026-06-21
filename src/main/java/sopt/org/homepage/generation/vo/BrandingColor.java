package sopt.org.homepage.generation.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandingColor {

    @Column(name = "\"darkModeKeyColor\"", nullable = true, length = 7)
    private String darkModeKeyColor;

    @Column(name = "\"darkModeTextColor\"", nullable = true, length = 5)
    private String darkModeTextColor;

    @Column(name = "\"lightModeKeyColor\"", nullable = true, length = 7)
    private String lightModeKeyColor;

    @Column(name = "\"lightModeTextColor\"", nullable = true, length = 5)
    private String lightModeTextColor;

    @Builder
    public BrandingColor(String darkModeKeyColor, String darkModeTextColor,
                         String lightModeKeyColor, String lightModeTextColor) {
        validateHexColor(darkModeKeyColor, "darkModeKeyColor");
        validateTextColor(darkModeTextColor, "darkModeTextColor");
        validateHexColor(lightModeKeyColor, "lightModeKeyColor");
        validateTextColor(lightModeTextColor, "lightModeTextColor");

        this.darkModeKeyColor = darkModeKeyColor;
        this.darkModeTextColor = darkModeTextColor;
        this.lightModeKeyColor = lightModeKeyColor;
        this.lightModeTextColor = lightModeTextColor;
    }

    private void validateHexColor(String color, String fieldName) {
        if (color == null || !color.matches("^[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException(
                    String.format("%s must be a valid hex color code (e.g., FF0000)", fieldName)
            );
        }
    }

    private void validateTextColor(String color, String fieldName) {
        if (color == null || (!color.equals("white") && !color.equals("black"))) {
            throw new IllegalArgumentException(
                    String.format("%s must be 'white' or 'black'", fieldName)
            );
        }
    }
}
