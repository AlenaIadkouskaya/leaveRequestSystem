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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.service.RequestService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/leave-requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/new")
    public ResponseEntity<UUID> createLeaveRequest(@AuthenticationPrincipal UserDetails currentUser,
                                                   @Valid @RequestBody RequestDto leaveRequestDto) {
        if (currentUser == null) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }
        UUID requestId = requestService.createLeaveRequest(currentUser.getUsername(), leaveRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(requestId);
    }

    @PatchMapping("/approve")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_HR')")
    public ResponseEntity<String> approveRequest(@AuthenticationPrincipal UserDetails currentUser,
                                                 @RequestParam UUID technicalId) throws AccessDeniedException, InterruptedException {
        requestService.approveRequest(currentUser.getUsername(), technicalId);
        return ResponseEntity.status(HttpStatus.OK).body("Request has been approved");
    }

    @PatchMapping("/reject")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_HR')")
    public ResponseEntity<String> rejectRequest(@AuthenticationPrincipal UserDetails currentUser,
                                                @RequestParam UUID technicalId) throws AccessDeniedException {
        requestService.rejectRequest(currentUser.getUsername(), technicalId);
        return ResponseEntity.status(HttpStatus.OK).body("Request has been rejected");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<RequestResponseDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_HR')")
    public ResponseEntity<RequestResponseDto> getRequestById(@PathVariable("id") UUID id) {
        RequestResponseDto responseDto = requestService.getRequestById(id);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/all-for-user")
    public List<RequestResponseDto> getAllRequestsByUserId(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return requestService.getRequestsByUser(username);
    }

    @GetMapping("/to-approve")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_HR')")
    public List<RequestResponseDto> getAllRequestsToApprove(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return requestService.getAllRequestsToApprove(username);
    }
}
