package com.oinkvalley.user_profile_svc.dto.profile;

import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/** 당분간 닉네임만 수정 (예전 코어 {@code User.changeUsername} 대응). */
public record PatchProfileRequest(@Size(max = 64) @Nullable String nickname) {
}
