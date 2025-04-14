package in.nnisarg.ezmail.user.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.nnisarg.ezmail.user.entity.User;
import in.nnisarg.ezmail.user.service.UserService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/users")
public class AuthController {
	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

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
}
