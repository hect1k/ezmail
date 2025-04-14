package in.nnisarg.ezmail.email.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	private String apiToken;

	private String fromName;
}
