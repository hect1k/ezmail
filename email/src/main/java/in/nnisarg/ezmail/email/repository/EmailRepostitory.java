package in.nnisarg.ezmail.email.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nnisarg.ezmail.email.entity.Email;

@Repository
public interface EmailRepostitory extends JpaRepository<Email, UUID> {
	List<Email> findByUserId(UUID userId);
}
