package sopt.org.homepage.admin.dao;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDao {
    private String role;    // 역할
    private String name;        // 이름
    private String affiliation; // 소속
    private String introduction; // 한줄 소개
    private String profileImage; // 프로필 사진
    private SnsLinksDao sns;
}

