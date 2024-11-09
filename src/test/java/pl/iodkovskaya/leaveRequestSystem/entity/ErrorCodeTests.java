package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorCodeTests {
    @Test
    public void should_return_correct_code_and_message() {
        // given
        ErrorCode errorCode = ErrorCode.NOT_FOUND;

        // when & then
        assertThat(errorCode.getCode()).isEqualTo(404);
        assertThat(errorCode.getMessage()).isEqualTo("Resource not found");
    }

    @Test
    public void should_return_string_representation() {
        // given
        ErrorCode errorCode = ErrorCode.DUPLICATE_ENTRY;

        // when
        String result = errorCode.toString();

        // then
        assertThat(result).isEqualTo("409: Duplicate entry: record already exists");
    }

    @Test
    public void should_contain_all_error_codes() {
        // given
        ErrorCode[] errorCodes = ErrorCode.values();

        // then
        assertThat(errorCodes).contains(ErrorCode.NOT_FOUND, ErrorCode.DATE_PROBLEM, ErrorCode.DUPLICATE_ENTRY,
                ErrorCode.BUSINESS_LOGIC_ERROR, ErrorCode.ACCESS_DENIED, ErrorCode.STATUS_CONFLICT,
                ErrorCode.ROLE_ALREADY_EXISTS, ErrorCode.USER_ALREADY_EXISTS);
    }

    @Test
    public void should_return_correct_message_for_user_already_exists() {
        // given
        ErrorCode errorCode = ErrorCode.USER_ALREADY_EXISTS;

        // when & then
        assertThat(errorCode.getMessage()).isEqualTo("User already exists: the specified user data conflicts");
    }

    @Test
    public void should_return_correct_code_for_status_conflict() {
        // given
        ErrorCode errorCode = ErrorCode.STATUS_CONFLICT;

        // when & then
        assertThat(errorCode.getCode()).isEqualTo(409);
    }
}
