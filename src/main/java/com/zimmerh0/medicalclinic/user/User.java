package com.zimmerh0.medicalclinic.user;

import com.zimmerh0.medicalclinic.dto.user.UserRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NonNull
    private String email;
    @Column(nullable = false)
    @NonNull
    private String password;

    public void update(UserRequest request) {
        email = request.email();
        password = request.password();
    }
    public void changePassword(String newPassword) {
        password = newPassword;
    }
}
