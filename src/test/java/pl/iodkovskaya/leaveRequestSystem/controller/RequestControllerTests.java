/*
 * Copyright 2024 Alena Iadkouskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.iodkovskaya.leaveRequestSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.iodkovskaya.leaveRequestSystem.mapper.RequestMapper;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;
import pl.iodkovskaya.leaveRequestSystem.service.RequestService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

public class RequestControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private RequestService requestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RequestMapper requestMapper;
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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "1", roles = "USER")
    public void should_create_request_successfully() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(userEntity, 20, 0, LocalDate.of(2024, 3, 12));
        vacationBalanceRepository.save(vacationBalance);
        RequestDto validLeaveRequestDto = new RequestDto(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()), 6);

        // when & then
        mockMvc.perform(post("/api/leave-requests/new")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validLeaveRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "1", roles = "USER")
    public void should_return_bad_request_when_invalid_input_data_when_creating_new_request() throws Exception {
        // given
        RequestDto validLeaveRequestDto = new RequestDto();

        // when & then
        mockMvc.perform(post("/api/leave-requests/new")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validLeaveRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_unauthorized_when_not_authenticated_when_creating_request() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(userEntity, 20, 0, LocalDate.of(2024, 5, 15));
        vacationBalanceRepository.save(vacationBalance);
        RequestDto validLeaveRequestDto = new RequestDto(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()), 6);

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
    public void should_return_unauthorized_when_user_not_authenticated_when_getting_all_requests() throws Exception {

        // when & then
        mockMvc.perform(get("/api/leave-requests"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "user@gmail.com", password = "1", roles = "USER")
    public void should_return_forbidden_when_user_not_have_role_manager_when_getting_all_requests() throws Exception {

        // when & then
        mockMvc.perform(get("/api/leave-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
    void should_approve_request_as_manager() throws Exception {
        // given
        UserEntity manager = new UserEntity("manager@gmail.com", "1", "LastName", "FirstName", "manager@gmail.com",
                new RoleEntity("ROLE_MANAGER"), true);
        userRepository.save(manager);
        UserEntity user = new UserEntity("user1@mail.com", "password", "LastName", "FirstName", "user1@mail.com", new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        RequestEntity request = new RequestEntity(user, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request);

        // when & then
        mockMvc.perform(patch("/api/leave-requests/approve")
                        .param("technicalId", request.getTechnicalId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Request has been approved"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "1", roles = "USER")
    void should_return_forbidden_when_user_not_have_role_manager_when_approving_request() throws Exception {
        // given
        UUID technicalId = UUID.randomUUID();

        // when & then
        mockMvc.perform(patch("/api/leave-requests/approve")
                        .param("technicalId", technicalId.toString()))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
    void should_return_not_found_when_request_not_found_when_approving_request() throws Exception {
        // given
        UUID invalidTechnicalId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Request not found with technical ID: " + invalidTechnicalId))
                .when(requestService).approveRequest(anyString(), eq(invalidTechnicalId));

        // when & then
        mockMvc.perform(patch("/approve")
                        .param("technicalId", invalidTechnicalId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void should_return_unauthorized_when_approving_request_when_user_not_authorized() throws Exception {
        // given
        UUID technicalId = UUID.randomUUID();

        // when & then
        mockMvc.perform(patch("/api/leave-requests/approve")
                        .param("technicalId", technicalId.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "hr@gmail.com", password = "1", roles = "HR")
    void should_reject_request_with_hr_role() throws Exception {
        // given
        UserEntity hr = new UserEntity("hr@gmail.com", "1", "LastName", "FirstName", "hr@gmail.com",
                new RoleEntity("ROLE_HR"), true);
        userRepository.save(hr);
        UserEntity user = new UserEntity("user1@mail.com", "password", "LastName", "FirstName", "user1@mail.com", new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 0, LocalDate.of(2024, 5, 15));
        vacationBalanceRepository.save(vacationBalance);
        RequestEntity request = new RequestEntity(user, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request);

        // when & then
        mockMvc.perform(patch("/api/leave-requests/reject")
                        .param("technicalId", request.getTechnicalId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Request has been rejected"));

    }

    @Test
    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
    void should_return_bad_request_when_request_with_id_not_exist_when_rejecting() throws Exception {
        mockMvc.perform(patch("/api/leave-requests/reject"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
    void should_get_request_by_id_with_manager_role() throws Exception {
        // given
        UserEntity manager = new UserEntity("manager@gmail.com", "1", "LastName", "FirstName", "manager@gmail.com",
                new RoleEntity("ROLE_MANAGER"), true);
        userRepository.save(manager);
        UserEntity user = new UserEntity("user1@mail.com", "password", "LastName", "FirstName", "user1@mail.com", new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        RequestEntity request = new RequestEntity(user, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request);

        // when & then
        mockMvc.perform(get("/api/leave-requests/" + request.getTechnicalId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "hr@gmail.com", password = "1", roles = "HR")
    void should_return_bad_request_when_id_has_invalid_UUID_format_when_getting_request_by_id() throws Exception {
        // given
        String invalidUUID = "invalid-uuid";

        // when & then
        mockMvc.perform(get("/api/leave-requests/" + invalidUUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", password = "1", roles = "USER")
    void should_return_all_requests_for_logged_user() throws Exception {
        // given
        UserEntity user = new UserEntity("test@gmail.com", "password", "LastName", "FirstName", "test@gmail.com",
                new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        RequestEntity request = new RequestEntity(user, RequestStatus.APPROVED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request);
        RequestEntity request2 = new RequestEntity(user, RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request2);
        List<RequestResponseDto> listRequests = List.of(requestMapper.fromEntity(request), requestMapper.fromEntity(request2));

        // when & then
        mockMvc.perform(get("/api/leave-requests/all-for-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(listRequests.size()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "nonExistentUser")
    void shoul_return_bad_request_when_logged_user_not_when_trying_to_get_all_requests() throws Exception {
        // when & then
        mockMvc.perform(get("/api/leave-requests/all-for-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), containsString("User not found")));
    }

    @Test
    @WithMockUser(username = "hr@gmail.com", password = "1", roles = "HR")
    void should_get_all_requests_to_approve_with_role_hr() throws Exception {
        // given
        UserEntity hr = new UserEntity("hr@gmail.com", "1", "LastName", "FirstName", "hr@gmail.com",
                new RoleEntity("ROLE_HR"), true);
        userRepository.save(hr);
        UserEntity user = new UserEntity("test@gmail.com", "password", "LastName", "FirstName", "test@gmail.com",
                new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        RequestEntity request = new RequestEntity(user, RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request);
        RequestEntity request2 = new RequestEntity(user, RequestStatus.APPROVED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request2);

        List<RequestResponseDto> listRequests = List.of(requestMapper.fromEntity(request));

        // when & then
        mockMvc.perform(get("/api/leave-requests/to-approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(listRequests.size()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "hr@gmail.com", password = "1", roles = "HR")
    void should_return_empty_list_when_requests_to_approve_not_found() throws Exception {
        // given
        UserEntity hr = new UserEntity("hr@gmail.com", "1", "LastName", "FirstName", "hr@gmail.com",
                new RoleEntity("ROLE_HR"), true);
        userRepository.save(hr);
        UserEntity user = new UserEntity("test@gmail.com", "password", "LastName", "FirstName", "test@gmail.com",
                new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        RequestEntity request = new RequestEntity(user, RequestStatus.REJECTED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request);
        RequestEntity request2 = new RequestEntity(user, RequestStatus.APPROVED, LocalDate.now(), LocalDate.now().plusDays(5));
        requestRepository.save(request2);

        // when & then
        mockMvc.perform(get("/api/leave-requests/to-approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
