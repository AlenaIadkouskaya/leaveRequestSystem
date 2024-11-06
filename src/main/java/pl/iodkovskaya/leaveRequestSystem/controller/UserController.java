package pl.iodkovskaya.leaveRequestSystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.service.RequestService;
import pl.iodkovskaya.leaveRequestSystem.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        userService.registerNewUser(userDto);
        return ResponseEntity.ok("User registered successfully");
    }
}
