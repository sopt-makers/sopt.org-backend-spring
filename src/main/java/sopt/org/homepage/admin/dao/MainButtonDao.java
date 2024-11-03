package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainButtonDao {
    private String text;
    private String keyColor;
    private String subColor;
}
