package sopt.org.homepage.project.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.SuperBuilder;
import sopt.org.homepage.project.dto.record.Category;
import sopt.org.homepage.project.dto.record.Link;
import sopt.org.homepage.project.dto.record.Member;
import sopt.org.homepage.project.dto.type.ServiceType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Getter
public class ProjectDetailResponseDto extends ProjectsResponseDto {


        @Schema(description = "프로젝트 시작 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
        private final LocalDate startAt;

        @Schema(description = "프로젝트 종료 날짜. 프로젝트가 진행중 일 경우 값 없음", nullable = true)
        private final LocalDate endAt;


        @Schema(description = "프로젝트 이미지 URL", nullable = true)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)  // 빈 문자열이나 null일 경우 JSON에서 제외
        private final String projectImage;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        @Schema(description = "프로젝트를 등록한 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        private final LocalDateTime uploadedAt;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        @Schema(description = "프로젝트를 수정한 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        private final LocalDateTime updatedAt;




        @Schema(description = "프로젝트 팀원", requiredMode = Schema.RequiredMode.REQUIRED)
        private final List<Member> members;

        public ProjectDetailResponseDto(Long id, String name, Integer generation, Category category, String projectImage,
                                        List<ServiceType> serviceType, String summary, String detail, String logoImage,
                                        String thumbnailImage, Boolean isAvailable, Boolean isFounding, List<Link> links,
                                        LocalDate startAt, LocalDate endAt, LocalDateTime uploadedAt,
                                        LocalDateTime updatedAt, List<Member> members) {

                super(id, name, generation, category, serviceType, summary, detail, logoImage,
                        thumbnailImage, isAvailable, isFounding, links);

                this.startAt = startAt;
                this.endAt = endAt;
                this.projectImage = projectImage;
                this.uploadedAt = uploadedAt;
                this.updatedAt = updatedAt;
                this.members = members;
        }
}
