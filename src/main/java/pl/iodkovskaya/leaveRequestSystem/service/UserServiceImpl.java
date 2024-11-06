package pl.iodkovskaya.leaveRequestSystem.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;

import java.util.Optional;

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
    public void registerNewUser(UserDto user) {
        try {
            userRepository.save(new UserEntity(user.getLogin(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getFirstName(), user.getLastName(), user.getEmail(),
                    roleService.findRoleByName("ROLE_USER"), true));
        } catch (RuntimeException e) {

        }
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findByUserId(id);
    }
}
