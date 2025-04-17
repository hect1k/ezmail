package in.nnisarg.ezmail.email.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.nnisarg.ezmail.email.entity.Email;
import in.nnisarg.ezmail.email.entity.User;
import in.nnisarg.ezmail.email.repository.EmailRepostitory;
import in.nnisarg.ezmail.email.repository.UserRepository;
import in.nnisarg.ezmail.email.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/emails")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private EmailRepostitory emailRepostitory;

	@Autowired
	private UserRepository userRepository;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SendEmailRequest {
		private String recipient;
		private String subject;
		private String body;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public class EmailResponse {
		private String to;
		private String subject;
		private String body;
		private java.time.LocalDateTime sentAt;
		private String status;
	}

	@PostMapping("/send")
	public ResponseEntity<?> sendEmail(
			@RequestHeader("Api-Token") String apiToken,
			@RequestBody SendEmailRequest request) {
		Optional<User> user = userRepository.findByApiToken(apiToken);

		if (!user.isPresent()) {
			return ResponseEntity.status(401).body("Invalid API token");
		}

		try {
			emailService.sendEmail(user.get().getId(), request.getRecipient(), request.getSubject(), request.getBody());
			return ResponseEntity.ok("Email sent successfully");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<EmailResponse>> getEmailsByUserId(
			@RequestHeader("X-User-ID") UUID userId) {

		List<Email> emails = emailRepostitory.findByUserId(userId);

		List<EmailResponse> emailResponses = emails.stream()
				.map(email -> new EmailResponse(email.getRecipient(), email.getSubject(), email.getBody(), email.getSentAt(),
						email.getStatus()))
				.toList();
		return ResponseEntity.ok(emailResponses);
	}
}
