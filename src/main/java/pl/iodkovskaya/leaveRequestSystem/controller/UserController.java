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

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.service.UserService;

import java.nio.file.AccessDeniedException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        Long userId = userService.registerNewUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(userId));
    }

    @PatchMapping("/add-role/{email}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> addRoleToUser(@AuthenticationPrincipal UserDetails currentUser, @PathVariable String email, @RequestParam String roleName) throws AccessDeniedException {

        userService.addRoleToUser(email, roleName, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body("Role has changed successfully");
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }
}
