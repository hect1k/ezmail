package in.nnisarg.ezmail.billing.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import in.nnisarg.ezmail.billing.entity.Billing;
import in.nnisarg.ezmail.billing.repository.BillingRepository;

@Service
public class BillingService {
	private final BillingRepository billingRepository;

	public BillingService(BillingRepository billingRepository) {
		this.billingRepository = billingRepository;
	}

	public Billing getBilling(UUID userId) {
		return billingRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Billing record not found for user"));
	}

	private int getPlanLimit(String plan) {
		return switch (plan.toUpperCase()) {
			case "FREE" -> 100;
			case "BASIC" -> 1000;
			case "PREMIUM" -> 10000;
			default -> throw new IllegalArgumentException("Unknown plan: " + plan);
		};
	}

	public void incrementUsage(UUID userId) {
		Billing billing = getBilling(userId);

		int limit = getPlanLimit(billing.getPlan());

		if (billing.getEmailsSent() >= limit) {
			throw new RuntimeException("Email limit reached for plan " + billing.getPlan());
		}

		billing.setEmailsSent(billing.getEmailsSent() + 1);
		billingRepository.save(billing);
	}

	public void resetUsage() {
		List<Billing> all = billingRepository.findAll();
		for (Billing billing : all) {
			billing.setEmailsSent(0);
			billing.setLastReset(LocalDateTime.now());
		}
		billingRepository.saveAll(all);
	}
}
