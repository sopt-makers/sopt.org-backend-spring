package sopt.org.homepage.admin.entity.sub;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsLinksEntity {
    private String email;      // ex. 000@sopt.org
    private String linkedin;   // ex. https://www.linkedin.com/...
    private String github;     // ex. https://github.com/...
    private String behance;    // ex. https://www.behance.net/...
}
