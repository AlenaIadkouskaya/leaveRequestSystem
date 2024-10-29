package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.role.RoleEntity;
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
