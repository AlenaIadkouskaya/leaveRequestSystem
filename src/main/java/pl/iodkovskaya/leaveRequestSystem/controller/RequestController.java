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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        CountDownLatch latch = new CountDownLatch(2);
//
//        executor.submit(() -> {
//            try {
//                latch.await();
//                requestService.approveRequest(currentUser.getUsername(), technicalId);
//                System.out.println("Thread 1: Request approved successfully");
//            } catch (Exception e) {
//                System.out.println("Thread 1 failed: " + e.getMessage());
//            }
//        });
//
//        executor.submit(() -> {
//            try {
//                latch.await();
//                requestService.approveRequest("manager@gmail.com", technicalId);
//                System.out.println("Thread 2: Request approved successfully");
//            } catch (Exception e) {
//                System.out.println("Thread 2 failed: " + e.getMessage() + e.getClass().getName());
//            }
//        });
//        latch.countDown();
//        latch.countDown();
//
//        executor.shutdown();
//        executor.awaitTermination(10, TimeUnit.SECONDS);

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
