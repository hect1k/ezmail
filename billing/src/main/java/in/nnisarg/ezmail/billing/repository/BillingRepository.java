package in.nnisarg.ezmail.billing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nnisarg.ezmail.billing.entity.Billing;

@Repository
public interface BillingRepository extends JpaRepository<Billing, UUID> {
}
