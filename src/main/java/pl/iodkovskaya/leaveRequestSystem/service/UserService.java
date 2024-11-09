package pl.iodkovskaya.leaveRequestSystem.service;


import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UserService {
    void registerOAuth2User(String login);

    void registerNewUser(UserDto user);

    UserEntity findUserByEmail(String email);

    UserEntity findUserById(Long id);

    void addRoleToUser(String email, String roleName, List<String> authorities) throws AccessDeniedException;
}
