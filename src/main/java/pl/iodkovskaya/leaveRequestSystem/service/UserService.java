package pl.iodkovskaya.leaveRequestSystem.service;


public interface UserService {
    void registerOAuth2User(String login);
    void registerNewUser(String login, String password, String email);
}
