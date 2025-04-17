package in.nnisarg.ezmail.email.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.nnisarg.ezmail.email.config.EmailConfig;
import in.nnisarg.ezmail.email.entity.Email;
import in.nnisarg.ezmail.email.entity.User;
import in.nnisarg.ezmail.email.repository.EmailRepostitory;
import in.nnisarg.ezmail.email.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private EmailConfig emailConfig;

	@Autowired
	private EmailRepostitory emailRepostitory;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Transactional
	public void sendEmail(UUID userId, String recipient, String subject, String body) throws Exception {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		try {
			String url = "http://BILLING/billing/{userId}/increment";
			restTemplate.postForEntity(url, null, Void.class, userId);
		} catch (Exception e) {
			throw new RuntimeException("Billing service failed (limit may be reached). Rolling back transaction.", e);
		}

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(recipient);
		helper.setSubject(subject);
		helper.setText(body, true);
		helper.setFrom(String.format("%s <%s>", user.getFromName(), emailConfig.getAddress()));

		mailSender.send(message);

		Email email = new Email();
		email.setUser(user);
		email.setRecipient(recipient);
		email.setSubject(subject);
		email.setBody(body);
		email.setSentAt(java.time.LocalDateTime.now());
		email.setStatus("SENT");
		emailRepostitory.save(email);
	}

	public List<Email> getEmails(UUID userId) {
		return emailRepostitory.findByUserId(userId);
	}
}
