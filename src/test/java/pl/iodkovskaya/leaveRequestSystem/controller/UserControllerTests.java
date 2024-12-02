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
import org.jobrunr.configuration.JobRunrConfiguration;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RoleRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "manager@gmail.com", password = "1", roles = "MANAGER")
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @MockBean
    private StorageProvider storageProvider;

    @MockBean
    private JobRunrConfiguration.JobRunrConfigurationResult jobRunr;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void should_register_user_successfully() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        roleRepository.save(roleUser);
        UserDto userDto = new UserDto("testUser", "password123", "testuser@example.com", "", "");

        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void should_return_bad_request_when_invalid_data() throws Exception {
        // given
        UserDto invalidUserDto = new UserDto();

        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_change_role_hr_for_user_successfully() throws Exception {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";
        RoleEntity roleHR = new RoleEntity(roleName);
        roleRepository.save(roleHR);
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", email,
                roleUser, true);
        userRepository.save(userEntity);

        // when & then
        mockMvc.perform(patch("/api/users/add-role/{email}", email)
                        .param("roleName", roleName))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString(), equalTo("Role has changed successfully")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void should_return_access_denied_if_user_not_have_manager_role_when_changing_role() throws Exception {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";
        RoleEntity roleHR = new RoleEntity(roleName);
        roleRepository.save(roleHR);
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", email,
                roleUser, true);
        userRepository.save(userEntity);

        //when & then
        mockMvc.perform(patch("/api/users/add-role/{email}", email)
                        .param("roleName", roleName))
                .andExpect(status().isForbidden());
    }

    @Test
    //@WithMockUser(roles = "MANAGER")
    public void should_return_not_found_when_user_not_found() throws Exception {
        // given
        String email = "nonexistent@example.com";
        String roleName = "ROLE_USER";

        // when & then
        mockMvc.perform(patch("/api/users/add-role/{email}", email)
                        .param("roleName", roleName))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(EntityNotFoundException.class)))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), containsString("User not found")));
    }

    @Test
    public void should_return_not_found_when_invalid_role_name() throws Exception {
        // given
        String email = "test@example.com";
        String roleName = "";
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", email,
                roleUser, true);
        userRepository.save(userEntity);

        // when & then
        mockMvc.perform(patch("/api/users/add-role/{email}", email)
                        .param("roleName", roleName))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_delete_user_successfully() throws Exception {
        // given
        RoleEntity roleUser = new RoleEntity("ROLE_USER");
        UserEntity userEntity = new UserEntity("user@gmail.com", "1", "LastName", "FirstName", "user@gmail.com",
                roleUser, true);
        userRepository.save(userEntity);
        Long userId = userEntity.getUserId();

        // when & then
        mockMvc.perform(delete("/api/users/delete/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

    }

    @Test
    public void should_return_not_found_if_user_not_found_when_deleting() throws Exception {
        // given
        Long userId = 999L;

        // when & then
        mockMvc.perform(delete("/api/users/delete/" + userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), containsString("User not found")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void should_return_access_denied_if_user_not_have_manager_role_when_deleting() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(delete("/api/users/delete/" + userId))
                .andExpect(status().isForbidden());
    }
}
