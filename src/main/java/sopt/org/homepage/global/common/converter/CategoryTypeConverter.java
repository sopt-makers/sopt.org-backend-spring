package sopt.org.homepage.global.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sopt.org.homepage.review.domain.vo.CategoryType;

/**
 * CategoryTypeConverter
 * <p>
 * CategoryType enum을 데이터베이스의 displayName 값으로 변환
 * <p>
 * DB에 저장된 값: "전체 활동", "서류/면접", "세미나", "프로젝트", "스터디", "기타" Java Enum: ACTIVITY, RECRUITING, SEMINAR, PROJECT, STUDY,
 * OTHER
 */
@Converter(autoApply = false)
public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {

    @Override
    public String convertToDatabaseColumn(CategoryType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDisplayName(); // "전체 활동", "서류/면접" 등의 값 반환
    }

    @Override
    public CategoryType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        // displayName으로 매칭
        return CategoryType.from(dbData);
    }
}
