package sopt.org.homepage.main.entity.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

