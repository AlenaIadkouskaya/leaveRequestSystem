package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RoleRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import javax.sql.DataSource;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
