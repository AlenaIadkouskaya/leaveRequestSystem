package pl.iodkovskaya.leaveRequestSystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.service.RequestService;
import pl.iodkovskaya.leaveRequestSystem.service.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

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

    @PatchMapping("/add-role/{email}")
    public ResponseEntity<String> addRoleToUser(@AuthenticationPrincipal User currentUser, @PathVariable String email, @RequestParam String roleName) throws AccessDeniedException {
        List<String> authorities = currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        userService.addRoleToUser(email, roleName, authorities);

        return ResponseEntity.status(HttpStatus.OK).body("Role has changed successfully");
    }
}
