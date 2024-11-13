package pl.iodkovskaya.leaveRequestSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RoleRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.service.VacationBalanceService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VacationBalanceControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private VacationBalanceService vacationBalanceService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "USER")
    void should_create_vacation_balance_when_valid_dto() throws Exception {
        // given
        RoleEntity roleEntity = roleRepository.findByRoleName("ROLE_USER").orElseThrow();
        UserEntity userEntity = new UserEntity("manager@gmail.com", "1", "LastName", "FirstName", "manager@gmail.com",
                roleEntity, true);
        userRepository.save(userEntity);
        VacationBalanceDto vacationBalanceDto = new VacationBalanceDto(userEntity.getUserId(), 20, 5);

        // when
        mockMvc.perform(post("/api/vacation-balance/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacationBalanceDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("The data regarding the remaining days has been successfully added"));

        // then
        //verify(vacationBalanceService).addRecord(vacationBalanceDto);
    }
}