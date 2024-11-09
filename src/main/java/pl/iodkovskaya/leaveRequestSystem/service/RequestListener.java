package pl.iodkovskaya.leaveRequestSystem.service;

import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

public interface RequestListener {
    void checkRemainderForUser(UserEntity user, Integer countDays);

    void decreaseRemainder(UserEntity user, RequestEntity request);

    void increaseRemainder(UserEntity user, RequestEntity request);
}
