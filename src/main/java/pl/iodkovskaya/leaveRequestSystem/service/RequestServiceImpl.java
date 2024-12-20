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

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.mapper.RequestMapper;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final RequestListener requestListener;
    private final RequestMapper requestMapper;
    private final LogService logService;
    private final JobScheduler jobScheduler;

    @Override
    @Transactional
    public UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto) {
        UserEntity userByEmail = findUserByEmail(userEmail);
        LocalDate startVacation = leaveRequestDto.getStartDate();
        LocalDate endVacation = leaveRequestDto.getStartDate().plusDays(leaveRequestDto.getDurationVacation() - 1);

        RequestEntity newRequest = new RequestEntity(
                userByEmail,
                RequestStatus.CREATED,
                startVacation,
                endVacation);

        requestListener.checkRemainderForUser(userByEmail, leaveRequestDto.getDurationVacation());

        if (hasOverlappingRequests(newRequest)) {
            throw new IllegalArgumentException("There is already a leave request for this period");
        }

        try {
            RequestEntity savedRequest = requestRepository.save(newRequest);
            requestListener.decreaseRemainder(userByEmail, savedRequest);
            return savedRequest.getTechnicalId();
        } catch (Exception e) {
            throw new RuntimeException("Error saving leave request: " + e.getMessage(), e);
        }
    }

    @Override
    public void approveRequest(String userEmail, UUID technicalId) {
        jobScheduler
                .enqueue(() -> approveRequestAsync(userEmail, technicalId));
    }

    @Transactional
    @Override
    public void approveRequestAsync(String userEmail, UUID technicalId) {

        UserEntity approver = findUserByEmail(userEmail);
        logService.logApprovalAttempt(technicalId, approver.getUserId(), "APPROVE");

        RequestEntity request = requestRepository.findByTechnicalId(technicalId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + technicalId));

        request.approve(approver);

    }

    @Override
    @Transactional
    public void rejectRequest(String userEmail, UUID technicalId) {
        UserEntity performer = findUserByEmail(userEmail);
        logService.logApprovalAttempt(technicalId, performer.getUserId(), "REJECT");

        RequestEntity request = requestRepository.findByTechnicalId(technicalId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + technicalId));

        request.reject();
        requestListener.increaseRemainder(request.getUser(), request);

    }

    @Override
    public List<RequestResponseDto> getAllRequests() {
        return requestRepository.findAll().stream().map(e -> requestMapper.fromEntity(e)).toList();
    }

    @Override
    public RequestResponseDto getRequestById(UUID id) {
        return requestRepository.findByTechnicalId(id)
                .map(requestMapper::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + id));
    }

    @Override
    public List<RequestResponseDto> getRequestsByUser(String username) {
        UserEntity user = findUserByEmail(username);
        List<RequestEntity> requests = requestRepository.findByUser(user);
        return requests.stream()
                .map(requestMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponseDto> getAllRequestsToApprove(String username) {
        UserEntity approver = findUserByEmail(username);
        List<RequestEntity> filteredRequests = findAllRequestsToApproveForCurrentApprover(approver);

        return filteredRequests.stream()
                .map(requestMapper::fromEntity)
                .collect(Collectors.toList());
    }

    private List<RequestEntity> findAllRequestsToApproveForCurrentApprover(UserEntity approver) {
        List<RequestEntity> allRequestsToApprove = requestRepository.findAllRequestsToApprove();

        return allRequestsToApprove.stream()
                .filter(request -> request.getApprovers().stream()
                        .noneMatch(approverInList -> approverInList.getRole().getRoleName().equals(approver.getRole().getRoleName())))
                .sorted(Comparator.comparing(RequestEntity::getStartDate))
                .toList();
    }


    private UserEntity findUserByEmail(String email) {
        UserEntity user = userService.findUserByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        return user;
    }


    private boolean hasOverlappingRequests(RequestEntity requestEntity) {

        return requestRepository.findAllByUserAndDateRange(
                requestEntity.getUser(),
                requestEntity.getStartDate(),
                requestEntity.getEndDate()).size() > 0;
    }
}
