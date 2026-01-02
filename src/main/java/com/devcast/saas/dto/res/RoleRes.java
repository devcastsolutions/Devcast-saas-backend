package com.devcast.saas.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRes {
    private Long roleId;
    private String name;
    private String description;
    private Set<PermissionRes> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
