package pl.iodkovskaya.leaveRequestSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
public class RequestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VacationBalanceRepository vacationBalanceRepository;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        vacationBalanceRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "1", roles = "USER")
    public void should_create_request_successfully() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(userEntity, 20, 0);
        vacationBalanceRepository.save(vacationBalance);
        RequestDto validLeaveRequestDto = new RequestDto(LocalDate.of(2024, 12, 1), 6);

        // when & then
        mockMvc.perform(post("/api/leave-requests/new")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validLeaveRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "1", roles = "USER")
    public void should_return_bad_request_when_invalid_input_data() throws Exception {
        // given
        RequestDto validLeaveRequestDto = new RequestDto();

        // when & then
        mockMvc.perform(post("/api/leave-requests/new")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validLeaveRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_unauthorized_when_not_authenticated() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(userEntity, 20, 0);
        vacationBalanceRepository.save(vacationBalance);
        RequestDto validLeaveRequestDto = new RequestDto(LocalDate.of(2024, 12, 1), 6);

        // when & then
        mockMvc.perform(post("/api/leave-requests/new")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validLeaveRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
    public void should_return_all_requests_for_manager_role() throws Exception {
        // given
        UserEntity user = new UserEntity("user1@mail.com", "password", "LastName", "FirstName", "user1@mail.com", new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);

        RequestEntity firstRequest = new RequestEntity(user, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(firstRequest);
        RequestEntity secondRequest = new RequestEntity(user, RequestStatus.CREATED, LocalDate.now().plusDays(30), LocalDate.now().plusDays(35));
        requestRepository.save(secondRequest);

        // when & then
        mockMvc.perform(get("/api/leave-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void should_return_unauthorizedget_when_user_not_authenticated() throws Exception {

        // when & then
        mockMvc.perform(get("/api/leave-requests"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "USER")
    public void should_return_forbidden_when_user_not_have_role_manager() throws Exception {

        // when & then
        mockMvc.perform(get("/api/leave-requests"))
                .andExpect(status().isForbidden());
    }
}
