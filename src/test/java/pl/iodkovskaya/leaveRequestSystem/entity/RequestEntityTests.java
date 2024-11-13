package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.exception.StatusException;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestEntityTests {
    @Test
    public void create_new_request_with_correct_data() {
        //given & when
        RequestEntity requestEntity = new RequestEntity(new UserEntity(), RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        //then
        assertThat(requestEntity).isNotNull();
    }

    @Test
    public void create_new_request_with_incorrect_startDate() {
        //given
        RequestEntity requestEntity = new RequestEntity(new UserEntity(), RequestStatus.CREATED, null, LocalDate.now().plusDays(5));
        //when
        Executable executable = () -> requestEntity.validateDates();
        //then
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void should_update_status_to_approved_when_all_roles_approve() {
        // given
        RequestEntity request = new RequestEntity();
        UserEntity approver1 = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_HR"), true);
        UserEntity approver2 = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_MANAGER"), true);

        // when
        request.approve(approver1);
        request.approve(approver2);

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.APPROVED);
        assertThat(request.getApprovers().size()).isEqualTo(2);
    }

    @Test
    void should_update_status_to_pending_when_only_one_role_approve() {
        // given
        RequestEntity request = new RequestEntity();
        UserEntity approver1 = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_HR"), true);

        // when
        request.approve(approver1);

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(request.getApprovers().size()).isEqualTo(1);
    }

    @Test
    void should_throw_status_exception_when_status_is_rejected() {
        // given
        RequestEntity request = new RequestEntity();
        request.updateStatus(RequestStatus.REJECTED);
        UserEntity approver1 = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_HR"), true);

        // when
        Executable e = () -> request.approve(approver1);

        // then
        assertThrows(StatusException.class, e);
        assertThat(request.getApprovers().size()).isEqualTo(0);
    }

    @Test
    void should_update_status_when_it_is_correct() {
        // given
        RequestEntity request = new RequestEntity();
        RequestStatus newStatus = RequestStatus.APPROVED;

        // when
        request.updateStatus(newStatus);

        // then
        assertThat(request.getStatus()).isEqualTo(newStatus);
    }

    @Test
    void should_throw_exception_when_status_is_null() {
        // given
        RequestEntity request = new RequestEntity();

        // when
        Executable e = () -> request.updateStatus(null);

        // then
        assertThrows(StatusException.class, e);
    }

    @Test
    public void should_set_status_reject() {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));

        // when
        request.reject();

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(request.getApprovers().size()).isEqualTo(0);
    }

    @Test
    public void should_clear_approvers_when_status_changed_to_reject() {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        UserEntity approver = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_HR"), true);
        request.approve(approver);

        // when
        request.reject();

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(request.getApprovers().size()).isEqualTo(0);
    }

    @Test
    public void should_throw_status_exception_when_already_request_rejected() {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        request.reject();

        // when
        Executable e = () -> request.reject();

        // then
        assertThrows(StatusException.class, e);
    }

    @Test
    void should_throws_exception_when_end_date_is_null() {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.CREATED, LocalDate.now(), null);

        // when
        Executable e = () -> request.validateDates();

        // then
        assertThrows(NullPointerException.class, e);
    }

    @Test
    void should_throws_exception_when_end_date_is_before_start_date() {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.CREATED, LocalDate.of(2024, 11, 10), LocalDate.of(2024, 11, 5));

        // when
        Executable e = () -> request.validateDates();

        // then
        assertThrows(IllegalArgumentException.class, e);
    }

    @Test
    void should_throw_exception_when_approver_has_duplicate_roles() {
        // given
        RequestEntity request = new RequestEntity();
        UserEntity approver1 = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_HR"), true);
        UserEntity approver2 = new UserEntity("", "", "", "", "", new RoleEntity("ROLE_HR"), true);
        request.approve(approver1);

        // when
        Executable e = () -> request.approve(approver2);

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(request.getApprovers().size()).isEqualTo(1);
        assertThrows(InvalidOperationException.class, e);
    }
}
