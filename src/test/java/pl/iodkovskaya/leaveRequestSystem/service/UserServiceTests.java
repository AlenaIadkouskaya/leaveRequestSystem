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
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.iodkovskaya.leaveRequestSystem.exception.RoleExistException;
import pl.iodkovskaya.leaveRequestSystem.exception.UserAlreadyExistsException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.UserDto;
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
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

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

    @Test
    public void should_return_user_when_user_with_email_exists() {
        // given
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity("", "", "", "", email, null, true);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);

        // when
        UserEntity foundUser = userService.findUserByEmail(email);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(email);
    }

    @Test
    public void should_return_null_when_user_not_exist() {
        // given
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity("", "", "", "", email, null, true);
        when(userRepository.findByEmail(email)).thenReturn(null);

        // when
        UserEntity foundUser = userService.findUserByEmail(email);

        // then
        assertThat(foundUser).isNull();
    }

    @Test
    public void should_return_user_when_user_with_id_exists() {
        // given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity(userId, "");
        when(userRepository.findByUserId(userId)).thenReturn(userEntity);

        // when
        UserEntity foundUser = userService.findUserById(userId);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(userId);
    }

    @Test
    public void should_return_user_when_user_with_id_not_exists() {
        // given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity(userId, "");
        when(userRepository.findByUserId(userId)).thenReturn(null);

        // when
        UserEntity foundUser = userService.findUserById(userId);

        // then
        assertThat(foundUser).isNull();
    }

    @Test
    public void should_throw_exception_when_user_already_exists() {
        // given
        UserDto userDto = new UserDto("testLogin", "password", "FirstName", "LastName", "test@example.com");
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(new UserEntity());

        // when
        Executable e = () -> userService.registerNewUser(userDto);

        // then
        assertThrows(UserAlreadyExistsException.class, e);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void should_register_new_user_successfully() {
        // given
        UserDto userDto = new UserDto("testLogin", "password", "FirstName", "LastName", "test@example.com");
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(null);
        RoleEntity roleEntity = mock(RoleEntity.class);
        when(roleService.findRoleByName("ROLE_USER")).thenReturn(roleEntity);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        UserEntity newUser = new UserEntity("testLogin", "encodedPassword", "FirstName", "LastName", "test@example.com", roleEntity, true);
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);

        // when
        userService.registerNewUser(userDto);

        // then
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void should_throw_exception_when_transaction_fails() {
        // given
        UserDto userDto = new UserDto("testLogin", "password", "FirstName", "LastName", "test@example.com");
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(null);
        when(roleService.findRoleByName("ROLE_USER")).thenReturn(mock(RoleEntity.class));
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        doThrow(new RuntimeException("Transaction failed")).when(userRepository).save(any(UserEntity.class));

        // when
        Executable e = () -> userService.registerNewUser(userDto);

        // then
        assertThrows(RuntimeException.class, e);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void should_register_OAuth2_user_successfully() {
        // given
        String login = "testUser";
        RoleEntity roleEntity = mock(RoleEntity.class);
        when(roleService.findRoleByName("ROLE_USER")).thenReturn(roleEntity);

        // when
        userService.registerOAuth2User(login);

        // then
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void should_throw_exception_when_save_fails() {
        // given
        String login = "testUser";
        RoleEntity roleEntity = mock(RoleEntity.class);
        when(roleService.findRoleByName("ROLE_USER")).thenReturn(roleEntity);
        doThrow(new RuntimeException("Failed to save user")).when(userRepository).save(any(UserEntity.class));

        // when
        Executable e = () -> userService.registerOAuth2User(login);

        // then
        assertThrows(RuntimeException.class, e);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
