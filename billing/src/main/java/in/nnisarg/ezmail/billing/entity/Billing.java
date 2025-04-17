package in.nnisarg.ezmail.billing.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "billing")
public class Billing {

	@Id
	private UUID userId;

	@Column(nullable = false)
	private String plan; // free, basic, premium

	@Column(nullable = false)
	private int emailsSent;

	@Column(nullable = false)
	private LocalDateTime lastReset;
}
