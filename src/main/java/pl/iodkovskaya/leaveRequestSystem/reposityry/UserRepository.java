package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(Long id);
    @Query(value = "SELECT COUNT(*) > 0 FROM request_approvers ra WHERE ra.user_id = :userId", nativeQuery = true)
    boolean existsApprovingRequestByUserId(@Param("userId") Long userId);
}
