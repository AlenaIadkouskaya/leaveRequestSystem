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
package pl.iodkovskaya.leaveRequestSystem.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "approval_logs")

@NoArgsConstructor
@Getter
public class ApprovalLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public ApprovalLogEntity(UUID leaveRequestId, Long managerId, String action, LocalDateTime timestamp) {
        this.requestId = leaveRequestId;
        this.userId = managerId;
        this.action = action;
        this.timestamp = timestamp;
    }
}
