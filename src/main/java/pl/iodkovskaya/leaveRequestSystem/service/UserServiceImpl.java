package pl.iodkovskaya.leaveRequestSystem.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerOAuth2User(String login) {
        userRepository.save(new UserEntity(login, "", "", "", "", roleService.findRoleByName("ROLE_USER"), true));
    }

    @Override
    public void registerNewUser(String login, String password, String email) {
        try {
            userRepository.save(new UserEntity(login,
                    passwordEncoder.encode(password),
                    "", "", email,
                    roleService.findRoleByName("ROLE_USER"), true));
        } catch (RuntimeException e) {

        }
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
