package pl.iodkovskaya.leaveRequestSystem.model.entity.role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "authorities")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

//    @OneToMany(mappedBy = "role")
//    private Set<UserEntity> users;

//    public RoleEntity(String roleName, Set<UserEntity> users) {
//        this.roleName = roleName;
//        this.users = users;
//    }

    public RoleEntity(String roleName) {
        this.roleName = roleName;
    }
}
