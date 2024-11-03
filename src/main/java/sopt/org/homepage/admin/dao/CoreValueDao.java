package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreValueDao {
    private String image;
    private String value;
    private String description;
}
