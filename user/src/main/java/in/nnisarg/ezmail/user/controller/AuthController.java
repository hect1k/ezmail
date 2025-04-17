package in.nnisarg.ezmail.user.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.nnisarg.ezmail.user.entity.User;
import in.nnisarg.ezmail.user.service.UserService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/users")
public class AuthController {
	@Autowired
	private UserService userService;

	@Autowired
	private RestTemplate restTemplate;

	@Getter
	@Setter
	public static class RegisterRequest {
		private String email;
		private String password;
		private String fromName;
	}

	@Getter
	@Setter
	public static class LoginRequest {
		private String email;
		private String password;
	}

	@Getter
	@Setter
	@Builder
	public static class TokenResponse {
		private String token;
	}

	@Getter
	@Setter
	@Builder
	public static class ProfileResponse {
		private String email;
		private String fromName;
		private String apiToken;
		private String plan;
		private int emailsSent;
		private LocalDateTime lastReset;
	}

	@Getter
	@Setter
	public static class BillingDTO {
		private UUID userId;
		private String plan;
		private int emailsSent;
		private LocalDateTime lastReset;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		User newUser = User.builder().email(request.getEmail()).password(request.getPassword())
				.fromName(request.getFromName()).build();

		User registeredUser = userService.register(newUser);
		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Optional<String> tokenOpt = userService.login(request.getEmail(), request.getPassword());

		if (tokenOpt.isPresent()) {
			return ResponseEntity.ok(new TokenResponse(tokenOpt.get()));
		} else {
			return ResponseEntity.status(401).body("Invalid credentials");
		}
	}

	@GetMapping("/profile")
	public ResponseEntity<?> profile(@RequestHeader("X-User-Id") UUID userId) {
		System.out.println("asdads");
		User user = userService.findById(userId).orElse(null);

		if (user == null) {
			return ResponseEntity.status(404).body("User not found");
		} else {
			ResponseEntity<BillingDTO> response = restTemplate.getForEntity(
					"http://BILLING/billing/" + user.getId().toString(),
					BillingDTO.class);

			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
				throw new RuntimeException("Failed to fetch billing info");
			}

			ResponseEntity<ProfileResponse> responseEntity = ResponseEntity
					.ok(new ProfileResponse(user.getEmail(), user.getFromName(),
							user.getApiToken(), response.getBody().getPlan(), response.getBody().getEmailsSent(),
							response.getBody().getLastReset()));
			return responseEntity;
		}
	}
}
