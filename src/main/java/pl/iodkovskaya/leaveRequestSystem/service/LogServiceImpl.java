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

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import pl.iodkovskaya.leaveRequestSystem.model.entity.ApprovalLogEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.ApprovalLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LogServiceImpl implements LogService {
    private final ApprovalLogRepository approvalLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logApprovalAttempt(UUID leaveRequestId, Long managerId, String action) {
        ApprovalLogEntity log = new ApprovalLogEntity(leaveRequestId, managerId, action, LocalDateTime.now());
        approvalLogRepository.save(log);
    }
}
