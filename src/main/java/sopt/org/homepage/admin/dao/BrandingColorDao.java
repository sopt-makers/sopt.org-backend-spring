package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandingColorDao {
    private String main;
    private String low;
    private String high;
    private String point;
}
