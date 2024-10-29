package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.user.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
