package pl.iodkovskaya.leaveRequestSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RoleRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.service.VacationBalanceService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
public class VacationBalanceControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VacationBalanceService vacationBalanceService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void should_create_vacation_balance_when_valid_dto() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceDto vacationBalanceDto = new VacationBalanceDto(userEntity.getUserId(), 20, 5);

        // when & then
        mockMvc.perform(post("/api/vacation-balance/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacationBalanceDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("The data regarding the remaining days has been successfully added"));
    }

    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "USER")
    @Test
    void should_throw_exception_when_have_not_rights() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceDto vacationBalanceDto = new VacationBalanceDto(userEntity.getUserId(), 20, 5);

        // when & then
        mockMvc.perform(post("/api/vacation-balance/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacationBalanceDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidDto() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceDto invalidDto = new VacationBalanceDto(null, 20, 5);

        // when & then
        mockMvc.perform(post("/api/vacation-balance/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}