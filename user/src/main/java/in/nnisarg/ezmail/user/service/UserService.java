package in.nnisarg.ezmail.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import in.nnisarg.ezmail.user.config.JwtUtil;
import in.nnisarg.ezmail.user.entity.User;
import in.nnisarg.ezmail.user.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RestTemplate restTemplate;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
			RestTemplate restTemplate) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.restTemplate = restTemplate;
	}

	@Getter
	@Setter
	private static class BillingRequest {
		private UUID userId;
		private String plan;
	}

	@Transactional
	public User register(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		savedUser.setApiToken(jwtUtil.generateToken(savedUser.getId()));
		userRepository.save(savedUser);

		BillingRequest billingRequest = new BillingRequest();
		billingRequest.setUserId(savedUser.getId());
		billingRequest.setPlan("FREE");

		try {
			restTemplate.postForEntity("http://BILLING/billing/register", billingRequest, Void.class);
		} catch (Exception e) {
			throw new RuntimeException("Billing service failed. Rolling back registration.", e);
		}

		return savedUser;
	}

	public Optional<String> login(String email, String rawPassword) {
		return userRepository.findByEmail(email).filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
				.map(user -> jwtUtil.generateToken(user.getId()));
	}

	public Optional<User> findById(UUID id) {
		return userRepository.findById(id);
	}
}
