package com.oinkvalley.user_profile_svc.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserProfileRequest(
    @NotNull Long userId,
    @NotBlank @Size(max = 64) String nickname) {
    
}
