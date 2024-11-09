package pl.iodkovskaya.leaveRequestSystem.model.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.exception.RoleExistException;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_role"))
    private RoleEntity role;

    private boolean enabled;

    public UserEntity(String username, String passwordHash, String lastName, String firstName, String email, RoleEntity role, boolean enabled) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public UserEntity(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public UserEntity(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }

    public void addRole(RoleEntity role) {
        if (this.role.equals(role)) {
            throw new RoleExistException("User already has this role " + role);
        }
        setRole(role);
    }

    private void setRole(RoleEntity role) {
        this.role = role;
    }
}
