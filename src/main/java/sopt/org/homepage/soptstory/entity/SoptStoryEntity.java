package sopt.org.homepage.soptstory.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "\"SoptStory\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptStoryEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "\"id\"", nullable = false)
	private Long id;

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
	@Column(name = "\"soptStoryUrl\"", nullable = false, length = 500)
	private String soptStoryUrl;

	@CreationTimestamp
	@Basic
	@Column(name = "\"createdAt\"", nullable = false)
	private LocalDateTime createdAt;

	@Basic
	@Column(name = "\"likeCount\"", nullable = false)
	private int likeCount=0;

	@OneToMany(mappedBy = "soptStroy", cascade = CascadeType.ALL)
	private List<SoptStoryLikeEntity> soptStoryLikes;

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		this.likeCount--;
	}

	@Builder
	private SoptStoryEntity(String thumbnailUrl, String title, String description, String soptStoryUrl) {
		this.thumbnailUrl = thumbnailUrl;
		this.title = title;
		this.description = description;
		this.soptStoryUrl = soptStoryUrl;
	}

}
