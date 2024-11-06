package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(Long id);
}
