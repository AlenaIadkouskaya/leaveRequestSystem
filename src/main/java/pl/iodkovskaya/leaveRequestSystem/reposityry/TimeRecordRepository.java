package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.timerecord.TimeRecordEntity;
@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecordEntity, Long> {
}
