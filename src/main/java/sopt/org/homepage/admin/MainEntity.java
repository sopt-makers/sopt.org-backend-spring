package sopt.org.homepage.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import sopt.org.homepage.admin.dao.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "\"Main\"")
public class MainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"generation\"", nullable = false)
    private Integer generation;

    @Column(name = "\"name\"", nullable = false)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"recruitSchedule\"", nullable = false, columnDefinition = "text")
    private List<RecruitScheduleDao> recruitSchedule;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"brandingColor\"", nullable = false, columnDefinition = "text")
    private BrandingColorDao brandingColor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"mainButton\"", nullable = false, columnDefinition = "text")
    private MainButtonDao mainButton;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"partIntroduction\"", nullable = false, columnDefinition = "text")
    private List<PartIntroductionDao> partIntroduction;

    @Column(name = "\"headerImage\"", nullable = false)
    private String headerImage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"coreValue\"", nullable = false, columnDefinition = "text")
    private List<CoreValueDao> coreValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"partCurriculum\"", nullable = false, columnDefinition = "text")
    private List<PartCurriculumDao> partCurriculum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"member\"", nullable = false, columnDefinition = "text")
    private List<MemberDao> member;

    @Column(name = "\"recruitHeaderImage\"", nullable = false)
    private String recruitHeaderImage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"recruitPartCurriculum\"", nullable = false, columnDefinition = "text")
    private List<RecruitPartCurriculumDao> recruitPartCurriculum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"recruitQuestion\"", nullable = false, columnDefinition = "text")
    private List<RecruitQuestionDao> recruitQuestion;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;
}

