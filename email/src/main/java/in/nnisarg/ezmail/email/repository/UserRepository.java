package in.nnisarg.ezmail.email.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nnisarg.ezmail.email.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByApiToken(String apiToken);
}
