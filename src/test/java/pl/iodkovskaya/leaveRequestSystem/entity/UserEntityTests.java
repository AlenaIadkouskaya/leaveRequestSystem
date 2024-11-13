package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.iodkovskaya.leaveRequestSystem.exception.RoleExistException;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserEntityTests {
    @Test
    void should_return_string_with_both_name_and_last_name() {
        // given
        UserEntity user = new UserEntity("login", "hashedpassword", "LastName", "fistName", "ivanov@example.com", null, true);

        // when
        String expected = "LastName fistName";

        // then
        assertThat(user.toString()).isEqualTo(expected);
    }

    @Test
    void should_return_string_with_empty_first_name() {
        // given
        UserEntity user = new UserEntity("login", "hashedpassword", "LastName", "", "ivanov@example.com", null, true);

        // when
        String expected = "LastName";

        // then
        assertThat(user.toString()).isEqualTo(expected);
    }

    @Test
    void should_return_string_with_empty_lats_name() {
        // given
        UserEntity user = new UserEntity("login", "hashedpassword", "", "FirstName", "ivanov@example.com", null, true);

        // when
        String expected = "FirstName";

        // then
        assertThat(user.toString()).isEqualTo(expected);
    }

    @Test
    void should_return_string_with_both_first_name_and_last_name_empty() {
        // given
        UserEntity user = new UserEntity("login", "hashedpassword", "", "", "ivanov@example.com", null, true);

        // when
        String expected = "";

        // then
        assertThat(user.toString()).isEqualTo(expected);
    }

    @Test
    void should_return_correct_string_with_spaces_in_name() {
        // given
        UserEntity user = new UserEntity("login", "hashedpassword", " LastName ", " fistName ", "ivanov@example.com", null, true);

        // when
        String expected = "LastName fistName";

        // then
        assertThat(user.toString()).isEqualTo(expected);
    }

    @Test
    void should_add_role_when_role_is_different() {
        // given
        RoleEntity userRole = new RoleEntity("USER");
        RoleEntity adminRole = new RoleEntity("ADMIN");
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "John", "Doe", "john.doe@example.com", userRole, true);

        // when
        user.addRole(adminRole);

        // then
        assertThat(user.getRole()).isEqualTo(adminRole);
    }

    @Test
    void should_throw_exception_when_role_is_same() {
        // given
        RoleEntity userRole = new RoleEntity("USER");
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "John", "Doe", "john.doe@example.com", userRole, true);

        // when
        Executable e = () -> user.addRole(userRole);

        // then
        assertThrows(RoleExistException.class, e);
    }
}

