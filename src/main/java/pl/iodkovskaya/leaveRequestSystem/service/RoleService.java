package pl.iodkovskaya.leaveRequestSystem.service;

import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;

public interface RoleService {
    RoleEntity findRoleByName(String name);
}
