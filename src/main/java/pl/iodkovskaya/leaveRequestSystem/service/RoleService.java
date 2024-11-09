package pl.iodkovskaya.leaveRequestSystem.service;

import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;

import java.util.Optional;

public interface RoleService {
    RoleEntity findRoleByName(String name);
}
