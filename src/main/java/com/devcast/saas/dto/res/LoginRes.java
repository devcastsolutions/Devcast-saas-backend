package com.devcast.saas.dto.res;

import com.devcast.saas.model.enums.UserRole;
import com.devcast.saas.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRes {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private UserRole role;
    private UserStatus status;
    private String profileImageUrl;
    private LocalDateTime lastLogin;
    private String token;
    private String refreshToken;
    private String message;
}
