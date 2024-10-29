package pl.iodkovskaya.leaveRequestSystem.model.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.user.UserEntity;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "authorities")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<UserEntity> users;
}
