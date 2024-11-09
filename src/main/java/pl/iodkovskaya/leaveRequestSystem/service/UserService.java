package pl.iodkovskaya.leaveRequestSystem.service;


import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void registerOAuth2User(String login);
    void registerNewUser(UserDto user);
    UserEntity findUserByEmail(String email);
    UserEntity findUserById(Long id);
    boolean addRoleToUser(String email, String roleName, List<String> authorities) throws AccessDeniedException;
}
