package pl.iodkovskaya.leaveRequestSystem.service;


import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

public interface UserService {
    void registerOAuth2User(String login);
    void registerNewUser(String login, String password, String email);
    UserEntity findUserByEmail(String email);
    UserEntity findUserById(Long id);
}
