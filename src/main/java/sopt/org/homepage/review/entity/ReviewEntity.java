package sopt.org.homepage.review.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.common.type.PartType;

@Entity
@Getter
@Table(name = "\"Review\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "\"id\"", nullable = false)
	private Long id;

	@Basic
	@Column(name = "\"title\"", nullable = false, length = 1000)
	private String title;

	@Basic
	@Column(name = "\"author\"", nullable = false, length = 20)
	private String author;

	@Basic
	@Column(name = "\"generation\"", nullable = false)
	private int generation;

	@Enumerated(EnumType.STRING)
	@Column(name = "\"part\"", nullable = false, length = 10)
	private PartType partType;

	@Basic
	@Column(name = "\"category\"", nullable = false, length = 20)
	private String category;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "\"subject\"", nullable = false, columnDefinition = "text")
	private List<String> subject;

	@Basic
	@Column(name = "\"thumbnailUrl\"", nullable = true, length = 500)
	private String thumbnailUrl;

	@Basic
	@Column(name = "\"platform\"", nullable = false, length = 50)
	private String platform;

	@Basic
	@Column(name = "\"url\"", nullable = false, length = 500)
	private String url;

	@Basic
	@Column(name = "\"description\"", nullable = false, length = 2000)
	private String description;

	@Basic
	@Column(name = "\"authorProfileImageUrl\"", nullable = true, length = 500)
	private String authorProfileImageUrl;

	@CreationTimestamp
	@Column(name = "\"createdAt\"", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "\"updatedAt\"", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public ReviewEntity(String title, String author, int generation, PartType partType, String category,
                        List<String> subject, String thumbnailUrl, String platform,
                        String url, String description, String authorProfileImageUrl) {
		this.title = title;
		this.author = author;
		this.generation = generation;
		this.partType = partType;
		this.category = category;
		this.subject = subject;
		this.thumbnailUrl = thumbnailUrl;
		this.platform = platform;
		this.url = url;
		this.description = description;
		this.authorProfileImageUrl = authorProfileImageUrl;
	}
}
