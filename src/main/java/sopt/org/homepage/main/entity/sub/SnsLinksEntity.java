package sopt.org.homepage.main.entity.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
