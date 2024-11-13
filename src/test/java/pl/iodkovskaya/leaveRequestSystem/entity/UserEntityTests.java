package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

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
}
