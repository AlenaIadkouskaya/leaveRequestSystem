package pl.iodkovskaya.leaveRequestSystem.service;


public interface UserService {
    void registerOAuth2User(String email, String name, String surname);
}
