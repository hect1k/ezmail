package in.nnisarg.ezmail.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.nnisarg.ezmail.user.config.JwtUtil;
import in.nnisarg.ezmail.user.entity.User;
import in.nnisarg.ezmail.user.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	public User register(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setApiToken(jwtUtil.generateToken(user.getId()));
		return userRepository.save(user);
	}

	public Optional<String> login(String email, String rawPassword) {
		return userRepository.findByEmail(email).filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
				.map(user -> jwtUtil.generateToken(user.getId()));
	}

	public Optional<User> findById(UUID id) {
		return userRepository.findById(id);
	}
}
