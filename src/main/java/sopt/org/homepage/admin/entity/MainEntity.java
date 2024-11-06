package sopt.org.homepage.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import sopt.org.homepage.admin.entity.sub.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "\"Main\"")
public class MainEntity {

    @Id
    @Column(name = "\"generation\"", nullable = false)
    private Integer generation;

    @Column(name = "\"name\"", nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"recruitSchedule\"", nullable = false, columnDefinition = "text")
    private List<RecruitScheduleEntity> recruitSchedule;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"brandingColor\"", nullable = false, columnDefinition = "text")
    private BrandingColorEntity brandingColor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"mainButton\"", nullable = false, columnDefinition = "text")
    private MainButtonEntity mainButton;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"partIntroduction\"", nullable = false, columnDefinition = "text")
    private List<PartIntroductionEntity> partIntroduction;

    @Column(name = "\"headerImage\"", nullable = false)
    private String headerImage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"coreValue\"", nullable = false, columnDefinition = "text")
    private List<CoreValueEntity> coreValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"partCurriculum\"", nullable = false, columnDefinition = "text")
    private List<PartCurriculumEntity> partCurriculum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"member\"", nullable = false, columnDefinition = "text")
    private List<MemberEntity> member;

    @Column(name = "\"recruitHeaderImage\"", nullable = false)
    private String recruitHeaderImage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"recruitPartCurriculum\"", nullable = false, columnDefinition = "text")
    private List<RecruitPartCurriculumEntity> recruitPartCurriculum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"recruitQuestion\"", nullable = false, columnDefinition = "text")
    private List<RecruitQuestionEntity> recruitQuestion;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;
}

