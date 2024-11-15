package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
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
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        if (userRepository.existsApprovingRequestByUserId(userId)) {
            throw new InvalidOperationException("Cannot delete user because they are an approver in an existing request.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public void registerOAuth2User(String login) {
        try {
            RoleEntity role = roleService.findRoleByName("ROLE_USER");
            userRepository.save(new UserEntity(login, "", "", "", login, role, true));
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed while registering new user", e);
        }
    }

    @Override
    public void registerNewUser(UserDto user) {
        UserEntity userEntity = userRepository.findByEmail(user.getEmail());

        if (userEntity != null) {
            throw new UserAlreadyExistsException("User with email: " + user.getEmail() + " exists!");
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
    public void addRoleToUser(String email, String roleName, UserDetails currentUser) throws AccessDeniedException {
        List<String> authorities = currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
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
        if (role == null) {
            throw new EntityNotFoundException("Role not exists");
        }
        user.addRole(role);
        userRepository.save(user);
    }
}
