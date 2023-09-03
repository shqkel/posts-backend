package com.sh.app.auth.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
	uniqueConstraints = 
		@UniqueConstraint(
				name = "uq_authority",
				columnNames = {"member_id", "auth"}))
@Data
@Builder
@NoArgsConstructor(force = true) // final필드, @NonNull필드를 기본생성자안에서 초기화 한다.
@AllArgsConstructor
public class Authority {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthEnum auth;
}
