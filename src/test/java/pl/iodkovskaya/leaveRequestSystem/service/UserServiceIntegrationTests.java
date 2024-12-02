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
package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RoleRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private VacationBalanceRepository vacationBalanceRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    @Transactional
    public void should_cascade_delete_associated_requests_when_user_is_deleted() {
        // given
        UserEntity user = new UserEntity("mailTest@gmail.com", "", "", "", "mailTest@gmail.com",
                new RoleEntity("ROLE_USER"), true);
        userRepository.save(user);
        RequestEntity request = new RequestEntity(user, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(1));
        requestRepository.save(request);

        // when
        userRepository.delete(user);

        // then
        assertThat(userRepository.findById(user.getUserId()).isPresent()).isFalse();
        assertThat(requestRepository.findAll().isEmpty());
    }

    @Test
    @Transactional
    public void should_cascade_delete_vacation_balance__when_user_is_deleted() {
        // given
        UserEntity user = new UserEntity("mailTest@gmail.com", "", "", "", "mailTest@gmail.com",
                new RoleEntity("ROLE_USER"), true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 0, LocalDate.of(2023, 9, 9));
        userRepository.save(user);

        // when
        userRepository.delete(user);

        // then
        assertThat(userRepository.findById(user.getUserId()).isPresent()).isFalse();
        assertThat(vacationBalanceRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    public void should_save_role_when_user_is_saved() {
        // given
        RoleEntity role = new RoleEntity("ROLE_USER");
        UserEntity user = new UserEntity("user@mail.com", "", "LastName", "FirstName", "user@mail.com", role, true);

        // when
        userRepository.save(user);

        // then
        assertThat(userRepository.findById(user.getUserId()).isPresent()).isTrue();
        assertThat(roleRepository.findById(role.getRoleId()).isPresent()).isTrue();
    }

    @Test
    @Transactional
    public void should_keep_associated_role_when_user_is_deleted() {
        // given
        RoleEntity role = new RoleEntity("ROLE_USER");
        UserEntity user = new UserEntity("user@mail.com", "", "LastName", "FirstName", "user@mail.com", role, true);
        userRepository.save(user);

        // when
        userRepository.delete(user);

        // then
        assertThat(userRepository.findById(user.getUserId()).isPresent()).isFalse();
        assertThat(roleRepository.findById(role.getRoleId()).isPresent()).isTrue();
    }
}
