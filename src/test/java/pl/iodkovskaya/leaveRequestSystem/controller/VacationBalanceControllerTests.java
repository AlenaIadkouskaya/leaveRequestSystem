package pl.iodkovskaya.leaveRequestSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.service.VacationBalanceService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    @InjectMocks
    private VacationBalanceController vacationBalanceController;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private Authentication authentication;

    @Test
    void createVacationBalance_ShouldReturnCreated_WhenValidDto() throws Exception {
        // given
        RoleEntity roleEntity = new RoleEntity("ROLE_USER");
        VacationBalanceDto vacationBalanceDto = new VacationBalanceDto(1L, 20, 5);
        UserEntity userEntity = new UserEntity("manager@gmail.com", "1", "LastName", "FirstName", "user1@mail.com",
                roleEntity, true);

        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(authentication.getName()).thenReturn(userEntity.getEmail());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        mockMvc.perform(post("/api/vacation-balance/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacationBalanceDto))
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("manager@gmail.com", "1")))
                .andExpect(status().isCreated())
                .andExpect(content().string("The data regarding the remaining days has been successfully added"));


        // then
        verify(vacationBalanceService).addRecord(vacationBalanceDto);
    }
}
