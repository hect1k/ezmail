package in.nnisarg.ezmail.billing.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.nnisarg.ezmail.billing.entity.Billing;
import in.nnisarg.ezmail.billing.repository.BillingRepository;
import in.nnisarg.ezmail.billing.service.BillingService;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/billing")
public class BillingController {

	@Autowired
	private BillingService billingService;

	@Autowired
	private BillingRepository billingRepository;

	@Getter
	@Setter
	private static class BillingRequest {
		private UUID userId;
		private String plan;
	}

	@PostMapping("/register")
	public ResponseEntity<?> createBilling(@RequestBody BillingRequest request) {
		Billing billing = new Billing();
		billing.setUserId(request.getUserId());
		billing.setPlan(request.getPlan());
		billing.setEmailsSent(0);
		billing.setLastReset(LocalDateTime.now());

		billingRepository.save(billing);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Billing> getBilling(@PathVariable UUID userId) {
		return ResponseEntity.ok(billingService.getBilling(userId));
	}

	@PostMapping("/{userId}/increment")
	public ResponseEntity<?> incrementUsage(@PathVariable UUID userId) {
		try {
			billingService.incrementUsage(userId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/reset")
	public ResponseEntity<?> resetAll() {
		billingService.resetUsage();
		return ResponseEntity.ok().build();
	}
}
