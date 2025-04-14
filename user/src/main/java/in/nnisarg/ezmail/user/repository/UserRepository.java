package in.nnisarg.ezmail.user.repository;

import org.springframework.stereotype.Repository;

import in.nnisarg.ezmail.user.entity.User;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);

	Optional<User> findById(UUID id);
}
