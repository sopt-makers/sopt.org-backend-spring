package sopt.org.homepage.sopticle.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "\"SopticleAuthor\"")
public class SopticleAuthorEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "\"id\"", nullable = false)
	private int id;

	@Basic
	@Column(name = "\"pgUserId\"", nullable = false)
	private Long pgUserId;

	@Basic
	@Column(name = "\"name\"", nullable = false, length = 20)
	private String name;

	@Basic
	@Column(name = "\"profileImage\"", nullable = true, length = 500)
	private String profileImage;

	@Basic
	@Column(name = "\"generation\"", nullable = false)
	private int generation;

	@Basic
	@Column(name = "\"part\"", nullable = false, length = 20)
	private String part;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"sopticleId\"")
	private SopticleEntity sopticle;

	@Builder
	private SopticleAuthorEntity(SopticleEntity sopticle, Long pgUserId, String name,
		String profileImage, Integer generation, String part) {
		this.sopticle = sopticle;
		this.pgUserId = pgUserId;
		this.name = name;
		this.profileImage = profileImage;
		this.generation = generation;
		this.part = part;
	}

}
