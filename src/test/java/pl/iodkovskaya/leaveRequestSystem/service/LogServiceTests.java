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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.iodkovskaya.leaveRequestSystem.model.entity.ApprovalLogEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.ApprovalLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceTests {
    private final ApprovalLogRepository logRepository = Mockito.mock(ApprovalLogRepository.class);
    @InjectMocks
    private LogServiceImpl logService;

    @Test
    void should_create_log_approval_successfully_when_reject_an_request() {
        // given
        UUID testRequestId = UUID.randomUUID();
        Long testUserId = 123L;
        ApprovalLogEntity logEntity = new ApprovalLogEntity(testRequestId, testUserId, "REJECT", LocalDateTime.now());

        // when
        logService.logApprovalAttempt(testRequestId, testUserId, "REJECT");

        // then
        verify(logRepository, times(1)).save(any(ApprovalLogEntity.class));
    }
    @Test
    void should_create_log_approval_successfully_when_approve_an_request() {
        // given
        UUID testRequestId = UUID.randomUUID();
        Long testUserId = 123L;
        ApprovalLogEntity logEntity = new ApprovalLogEntity(testRequestId, testUserId, "APPROVE", LocalDateTime.now());

        // when
        logService.logApprovalAttempt(testRequestId, testUserId, "APPROVE");

        // then
        verify(logRepository, times(1)).save(any(ApprovalLogEntity.class));
    }
}
