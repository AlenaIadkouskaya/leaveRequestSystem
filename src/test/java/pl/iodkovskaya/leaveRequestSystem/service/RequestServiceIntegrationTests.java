package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.model.entity.ApprovalLogEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.*;

import javax.sql.DataSource;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class RequestServiceIntegrationTests {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestService requestService;
    @Autowired
    private VacationBalanceRepository vacationBalanceRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ApprovalLogRepository approvalLogRepository;
    private final RoleEntity ROLE = new RoleEntity(1L, "ROLE_USER", new HashSet<>());
    @Autowired
    private DataSource dataSource;

    @Test
    public void testDatasource() {
        assertNotNull(dataSource);
    }

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        roleRepository.save(ROLE);
    }

    @Test
    @Transactional
    public void should_reject_request_successfully() throws AccessDeniedException {
        // given
        int daysRequested = 5;
        String userEmail = "user@example.com";

        UserEntity user = new UserEntity("", "", "", "", userEmail, ROLE, true);
        userRepository.save(user);


        RequestEntity request = new RequestEntity(user, RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(daysRequested));
        requestRepository.save(request);

        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 15, 6);
        vacationBalanceRepository.save(vacationBalance);

        // when
        requestService.rejectRequest(userEmail, request.getTechnicalId());
        RequestEntity updatedRequest = requestRepository.findByTechnicalId(request.getTechnicalId())
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        VacationBalanceEntity vacationBalanceForUserAfterReject = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Data not found"));

        // then
        assertThat(requestRepository.findByTechnicalId(request.getTechnicalId())).isNotNull();
        assertThat(updatedRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(vacationBalanceForUserAfterReject.getUsedDays()).isEqualTo(0);
        assertThat(vacationBalanceForUserAfterReject.getRemainingDays()).isEqualTo(15);
    }

    @Test
    void should_execute_inner_transaction_when_request_needs_to_reject_but_request_with_this_id_not_found() {
        // given
        UUID testRequestId = UUID.randomUUID();
        String testUserEmail = "userTest@example.com";
        RoleEntity roleEntity = new RoleEntity("admin");
        roleRepository.save(roleEntity);
        UserEntity user = new UserEntity("login", "1", "first", "last", testUserEmail, roleEntity, true);
        userRepository.save(user);

        // when
        Executable e = () -> requestService.rejectRequest(testUserEmail, testRequestId);

        // then
        assertThrows(EntityNotFoundException.class, e);
        ApprovalLogEntity logEntry = approvalLogRepository.findByRequestId(testRequestId);
        assertThat(logEntry).isNotNull();
        assertThat(logEntry.getRequestId()).isEqualTo(testRequestId);
        assertThat(logEntry.getAction()).isEqualTo("REJECT");
    }
    @Test
    void should_execute_inner_transaction_when_request_needs_to_approve_but_request_with_this_id_not_found() {
        // given
        UUID testRequestId = UUID.randomUUID();
        String testUserEmail = "test_user@example.com";
        RoleEntity roleEntity = new RoleEntity("admin");
        roleRepository.save(roleEntity);
        UserEntity user = new UserEntity("login", "1", "first", "last", testUserEmail, roleEntity, true);
        userRepository.save(user);

        // when
        Executable e = () -> requestService.approveRequest(testUserEmail, testRequestId);

        // then
        assertThrows(EntityNotFoundException.class, e);
        ApprovalLogEntity logEntry = approvalLogRepository.findByRequestId(testRequestId);
        assertThat(logEntry).isNotNull();
        assertThat(logEntry.getRequestId()).isEqualTo(testRequestId);
        assertThat(logEntry.getAction()).isEqualTo("APPROVE");
    }
}
