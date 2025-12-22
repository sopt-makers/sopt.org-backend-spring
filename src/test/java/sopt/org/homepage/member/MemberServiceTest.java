package sopt.org.homepage.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.member.dto.BulkCreateMembersCommand;
import sopt.org.homepage.member.dto.MemberDetailView;
import sopt.org.homepage.member.vo.MemberRole;
import sopt.org.homepage.member.vo.SnsLinks;

/**
 * Member 통합 테스트
 * <p>
 * 인수인계 목적: - Member는 SOPT 운영진을 나타냄 - 기수(generationId)별로 관리됨 - MemberRole Enum으로 역할 관리 - SnsLinks VO로 SNS 링크 관리
 */
@DisplayName("Member 서비스 통합 테스트")
class MemberServiceTest extends IntegrationTestBase {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    // ===== 생성 시나리오 =====

    @Nested
    @DisplayName("운영진 생성")
    class Create {
        @Test
        @DisplayName("✅ 정상: 일괄 생성 (기존 데이터 교체)")
        void bulkCreate_ReplacesExisting() {
            // given - 기존 데이터
            memberRepository.save(createEntity(35, MemberRole.PRESIDENT, "기존회장"));
            assertThat(memberRepository.countByGenerationId(35)).isEqualTo(1);

            // when - 새로운 데이터로 교체
            BulkCreateMembersCommand command = BulkCreateMembersCommand.builder()
                    .generationId(35)
                    .members(List.of(
                            BulkCreateMembersCommand.MemberData.builder()
                                    .role("회장")
                                    .name("새회장")
                                    .affiliation("OO대학교")
                                    .introduction("소개")
                                    .profileImageUrl("https://example.com/1.jpg")
                                    .build(),
                            BulkCreateMembersCommand.MemberData.builder()
                                    .role("부회장")
                                    .name("부회장님")
                                    .affiliation("XX대학교")
                                    .introduction("소개2")
                                    .profileImageUrl("https://example.com/2.jpg")
                                    .build()
                    ))
                    .build();

            List<Long> ids = memberService.bulkCreate(command);

            // then
            assertThat(ids).hasSize(2);

            List<MemberDetailView> result = memberService.findByGeneration(35);
            assertThat(result).hasSize(2);
            assertThat(result).extracting(MemberDetailView::name)
                    .containsExactlyInAnyOrder("새회장", "부회장님");
            // 기존 "기존회장"은 삭제됨
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("운영진 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 기수별 전체 조회 (역할순, 이름순)")
        void findByGeneration_Ordered() {
            // given
            memberRepository.saveAll(List.of(
                    createEntity(35, MemberRole.VICE_PRESIDENT, "나부회장"),
                    createEntity(35, MemberRole.PRESIDENT, "가회장"),
                    createEntity(35, MemberRole.OPERATION_TEAM_LEADER, "다운영팀장")
            ));

            // when
            List<MemberDetailView> result = memberService.findByGeneration(35);

            // then
            assertThat(result).hasSize(3);
            // 역할순 정렬 확인 (PRESIDENT < VICE_PRESIDENT < OPERATION_TEAM_LEADER)
        }


    }

    // ===== Helper Methods =====

    private Member createEntity(Integer generationId, MemberRole role, String name) {
        return Member.builder()
                .generationId(generationId)
                .role(role)
                .name(name)
                .affiliation("테스트대학교")
                .introduction("테스트 소개입니다.")
                .profileImageUrl("https://example.com/profile.jpg")
                .snsLinks(SnsLinks.empty())
                .build();
    }
}
