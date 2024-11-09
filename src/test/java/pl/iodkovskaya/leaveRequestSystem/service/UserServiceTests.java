package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import pl.iodkovskaya.leaveRequestSystem.exception.RoleExistException;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);


    private final RoleService roleService = Mockito.mock(RoleService.class);

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void should_add_role_to_user_successfully() throws AccessDeniedException {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";

        User currentUser = Mockito.mock(User.class);
        GrantedAuthority roleManager = new SimpleGrantedAuthority("ROLE_MANAGER");
        when(currentUser.getAuthorities()).thenReturn(List.of(roleManager));

        RoleEntity roleEntity = new RoleEntity(roleName);
        UserEntity userEntity = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_USER"), true);

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleService.findRoleByName(roleName)).thenReturn(roleEntity);

        // when
        userService.addRoleToUser(email, roleName, currentUser);

        // then
        verify(userRepository, times(1)).save(userEntity);
        assertThat(userEntity.getRole()).isEqualTo(roleEntity);
    }

    @Test
    public void should_throw_exception_when_user_has_no_access_to_set_role() {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";

        User currentUser = Mockito.mock(User.class);
        GrantedAuthority roleManager = new SimpleGrantedAuthority("ROLE_USER");
        when(currentUser.getAuthorities()).thenReturn(List.of(roleManager));

        // when
        Executable e = () -> userService.addRoleToUser(email, roleName, currentUser);

        // then
        assertThrows(AccessDeniedException.class, e);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void should_throw_exception_when_user_not_found() {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";

        User currentUser = Mockito.mock(User.class);
        GrantedAuthority roleManager = new SimpleGrantedAuthority("ROLE_MANAGER");
        when(currentUser.getAuthorities()).thenReturn(List.of(roleManager));

        when(userRepository.findByEmail(email)).thenReturn(null);

        // when
        Executable e = () -> userService.addRoleToUser(email, roleName, currentUser);

        // then
        assertThrows(EntityNotFoundException.class, e);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void should_throw_exception_when_role_not_found() {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";

        User currentUser = Mockito.mock(User.class);
        GrantedAuthority roleManager = new SimpleGrantedAuthority("ROLE_MANAGER");
        when(currentUser.getAuthorities()).thenReturn(List.of(roleManager));

        RoleEntity roleEntity = new RoleEntity(roleName);
        UserEntity userEntity = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_USER"), true);

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleService.findRoleByName(roleName)).thenReturn(null);

        // when
        Executable e = () -> userService.addRoleToUser(email, roleName, currentUser);

        // then
        assertThrows(EntityNotFoundException.class, e);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
    @Test
    public void should_throw_exception_when_user_already_has_this_role() {
        // given
        String email = "test@example.com";
        String roleName = "ROLE_HR";

        User currentUser = Mockito.mock(User.class);
        GrantedAuthority roleManager = new SimpleGrantedAuthority("ROLE_MANAGER");
        when(currentUser.getAuthorities()).thenReturn(List.of(roleManager));

        RoleEntity roleEntity = new RoleEntity(roleName);
        UserEntity userEntity = new UserEntity("", "", "", "", "", roleEntity, true);

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(roleService.findRoleByName(roleName)).thenReturn(roleEntity);

        // when
        Executable e = () -> userService.addRoleToUser(email, roleName, currentUser);

        // then
        assertThrows(RoleExistException.class, e);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
