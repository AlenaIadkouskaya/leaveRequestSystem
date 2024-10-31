package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
}
