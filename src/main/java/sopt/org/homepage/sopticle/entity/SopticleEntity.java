package sopt.org.homepage.sopticle.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.common.type.Part;

@Entity
@Getter
@Table(name = "\"Sopticle\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SopticleEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "\"id\"", nullable = false)
	private Long id;

	@Getter
	@Enumerated(EnumType.STRING)
	@Column(name = "\"part\"")
	private Part part;

	@Basic
	@Column(name = "\"generation\"", nullable = false)
	private int generation;

	@Basic
	@Column(name = "\"thumbnailUrl\"", nullable = true, length = 500)
	private String thumbnailUrl;

	@Basic
	@Column(name = "\"title\"", nullable = false, length = 100)
	private String title;

	@Basic
	@Column(name = "\"description\"", nullable = false, length = 600)
	private String description;

	@Basic
	@Column(name = "\"sopticleUrl\"", nullable = false, length = 500)
	private String sopticleUrl;

	@CreationTimestamp
	@Basic
	@Column(name = "\"createdAt\"", nullable = false)
	private LocalDateTime createdAt;

	@Basic
	@Column(name = "\"likeCount\"", nullable = false)
	private int likeCount;

	@OneToMany(mappedBy = "sopticle", cascade = CascadeType.ALL)
	private List<SopticleLikeEntity> sopticleLikes;

	@OneToOne(mappedBy = "sopticle", cascade = CascadeType.ALL, orphanRemoval = true)
	private SopticleAuthorEntity author;

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		this.likeCount--;
	}

	@Builder
	private SopticleEntity(Part part, Integer generation, String thumbnailUrl, String title,
		String description, String sopticleUrl) {
		this.part = part;
		this.generation = generation;
		this.thumbnailUrl = thumbnailUrl;
		this.title = title;
		this.description = description;
		this.sopticleUrl = sopticleUrl;
	}

}
