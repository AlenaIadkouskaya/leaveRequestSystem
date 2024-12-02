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

import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface RequestService {
    UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto);
    void approveRequest(String userEmail, UUID technicalId) throws AccessDeniedException;
    void rejectRequest(String userEmail, UUID technicalId) throws AccessDeniedException;
    List<RequestResponseDto> getAllRequests();
    RequestResponseDto getRequestById(UUID id);
    List<RequestResponseDto> getRequestsByUser(String userEmail);
    List<RequestResponseDto> getAllRequestsToApprove(String username);
    void approveRequestAsync(String userEmail, UUID technicalId);
}
