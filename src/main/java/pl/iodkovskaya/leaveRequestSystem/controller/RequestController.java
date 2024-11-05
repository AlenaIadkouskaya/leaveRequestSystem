package pl.iodkovskaya.leaveRequestSystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
//import pl.iodkovskaya.leaveRequestSystem.model.dto.ChangeStatusDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.service.RequestService;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/leave-requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/new")
    public ResponseEntity<UUID> createLeaveRequest(@AuthenticationPrincipal Object currentUser,
                                                   @Valid @RequestBody RequestDto leaveRequestDto) {
        UUID requestId = requestService.createLeaveRequest(((User) currentUser).getUsername(), leaveRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(requestId);
    }

    @PatchMapping("/approve")
    public ResponseEntity<String> approveRequest(@AuthenticationPrincipal Object currentUser,
                                                 @RequestParam UUID technicalId) throws AccessDeniedException {
        requestService.approveRequest(((User) currentUser).getUsername(), technicalId);
        return ResponseEntity.status(HttpStatus.OK).body("Request has been approved.");
    }
}
