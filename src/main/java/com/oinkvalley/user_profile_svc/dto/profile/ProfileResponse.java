package com.oinkvalley.user_profile_svc.dto.profile;

/**
 * 보드·프론트에 내려줄 최소 표시 정보. 예전 코어 {@code User.username} 과 같은 역할이 {@code nickname} 이다.
 */
public record ProfileResponse(long userId, String nickname) {
}
