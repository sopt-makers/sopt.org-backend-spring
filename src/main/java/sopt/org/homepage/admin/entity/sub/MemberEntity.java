package sopt.org.homepage.admin.entity.sub;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity {
    private String role;    // 역할
    private String name;        // 이름
    private String affiliation; // 소속
    private String introduction; // 한줄 소개
    private String profileImage; // 프로필 사진
    private SnsLinksEntity sns;
}

