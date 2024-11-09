package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.exception.UserAlreadyExistsException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
        UserEntity userEntity = userRepository.findByEmail(user.getEmail());

        if (userEntity != null) {
            throw new UserAlreadyExistsException("User with email: " + user.getEmail()+" exists!");
        }
        try {
            userRepository.save(new UserEntity(user.getLogin(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getLastName(),
                    user.getFirstName(),
                    user.getEmail(),
                    roleService.findRoleByName("ROLE_USER"),
                    true));
        } catch (RuntimeException e) {
            throw new RuntimeException("Transaction failed while registering new user", e);
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

    @Override
    public void addRoleToUser(String email, String roleName, List<String> authorities) throws AccessDeniedException {
        if (!authorities.contains("ROLE_MANAGER")) {
            throw new AccessDeniedException("Forbidden: You do not have permission to update this user");
        }
        updateUser(email, roleName);
    }

    private void updateUser(String email, String roleName) {

        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }

        RoleEntity role = roleService.findRoleByName(roleName);

        user.addRole(role);

        userRepository.save(user);
    }
}
