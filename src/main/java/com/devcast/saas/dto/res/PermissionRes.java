package com.devcast.saas.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRes {
    private Long permissionId;
    private String name;
    private String description;
    private String resource;
    private String action;
    private LocalDateTime createdAt;
}
