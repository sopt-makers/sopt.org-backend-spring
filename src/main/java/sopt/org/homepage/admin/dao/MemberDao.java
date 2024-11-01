package sopt.org.homepage.admin.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDao {
    private String role;    // 역할
    private String name;        // 이름
    private String affiliation; // 소속
    private String introduction; // 한줄 소개
    private String profileImage; // 프로필 사진
    private SnsLinks sns;
}

@Getter
@Setter
@NoArgsConstructor
class SnsLinks {
    private String email;      // ex. 000@sopt.org
    private String linkedin;   // ex. https://www.linkedin.com/...
    private String github;     // ex. https://github.com/...
    private String behance;    // ex. https://www.behance.net/...
}
