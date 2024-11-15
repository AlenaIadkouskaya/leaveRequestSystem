package pl.iodkovskaya.leaveRequestSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RoleRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.LocalDate;
import java.util.UUID;

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
}
