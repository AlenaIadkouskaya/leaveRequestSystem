package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
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

}
