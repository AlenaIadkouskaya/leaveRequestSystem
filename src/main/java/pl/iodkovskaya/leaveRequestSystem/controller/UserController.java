package pl.iodkovskaya.leaveRequestSystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.service.RequestService;
import pl.iodkovskaya.leaveRequestSystem.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RequestService requestService;
    private final UserService userService;
    //@PreAuthorize("hasRole('USER')")
    @PostMapping("/leaverequests")
    public void createLeaveRequest(@AuthenticationPrincipal Object currentUser,
                                                           @RequestBody RequestDto leaveRequestDto) {
        System.out.println();
        //requestService.createLeaveRequest(currentUser, leaveRequestDto);
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        userService.registerNewUser(userDto.getLogin(), userDto.getPassword(), userDto.getEmail());
        return ResponseEntity.ok("User registered successfully");
    }
}
