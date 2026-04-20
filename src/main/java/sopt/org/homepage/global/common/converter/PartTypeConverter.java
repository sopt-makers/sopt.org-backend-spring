package sopt.org.homepage.global.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sopt.org.homepage.global.common.type.PartType;

@Component
public class PartTypeConverter implements Converter<String, PartType> {

    @Override
    public PartType convert(String source) {
        return PartType.fromJson(source);
    }
}
