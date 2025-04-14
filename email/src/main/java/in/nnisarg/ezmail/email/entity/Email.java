package in.nnisarg.ezmail.email.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emails")
public class Email {

	@Id
	@GeneratedValue
	private UUID id;

	private String recipient;
	private String subject;

	@Lob
	private String body;

	private LocalDateTime sentAt;

	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}
