package sopt.org.homepage.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectResponse {

        @Schema(description = "프로젝트의 Id", requiredMode = Schema.RequiredMode.REQUIRED)
        private final Long id;

        @Schema(description = "프로젝트의 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        private final String name;

        @Schema(description = "프로젝트가 진행된 기수", requiredMode = Schema.RequiredMode.REQUIRED)
        private final Integer generation;

        @Schema(description = "프로젝트의 카테고리", requiredMode = Schema.RequiredMode.REQUIRED)
        private final Category category;

        @Schema(description = "서비스 형태", requiredMode = Schema.RequiredMode.REQUIRED)
        private final List<ServiceType> serviceType;

        @Schema(description = "프로젝트 한줄소개", requiredMode = Schema.RequiredMode.REQUIRED)
        private final String summary;

        @Schema(description = "프로젝트 설명", requiredMode = Schema.RequiredMode.REQUIRED)
        private final String detail;

        @Schema(description = "프로젝트 로고 이미지 URL", requiredMode = Schema.RequiredMode.REQUIRED)
        private final String logoImage;

        @Schema(description = "프로젝트 썸네일 이미지 URL", requiredMode = Schema.RequiredMode.REQUIRED)
        private final String thumbnailImage;

        @Schema(description = "서비스 이용 가능 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        private final Boolean isAvailable;

        @Schema(description = "창업중인지 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        private final Boolean isFounding;

        @Schema(description = "프로젝트 링크", requiredMode = Schema.RequiredMode.REQUIRED)
        private final List<Link> links;

        public ProjectResponse(Long id, String name, Integer generation, Category category, List<ServiceType> serviceType,
                               String summary, String detail, String logoImage, String thumbnailImage, Boolean isAvailable,
                               Boolean isFounding, List<Link> links) {
                this.id = id;
                this.name = name;
                this.generation = generation;
                this.category = category;
                this.serviceType = serviceType;
                this.summary = summary;
                this.detail = detail;
                this.logoImage = logoImage;
                this.thumbnailImage = thumbnailImage;
                this.isAvailable = isAvailable;
                this.isFounding = isFounding;
                this.links = links;


        }
}


