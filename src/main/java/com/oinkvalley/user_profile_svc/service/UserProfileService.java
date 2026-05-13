package com.oinkvalley.user_profile_svc.service;

import com.oinkvalley.user_profile_svc.db.domain.UserProfile;
import com.oinkvalley.user_profile_svc.db.repository.UserProfileRepository;
import com.oinkvalley.user_profile_svc.dto.profile.PatchProfileRequest;
import com.oinkvalley.user_profile_svc.dto.profile.ProfileResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    /** 배치: 요청 순서 유지, 없는 id 생략 (`/profiles?ids`). */
    @Transactional(readOnly = true)
    public List<ProfileResponse> batchByIdsParam(String idsParam) {
        List<Long> ids = parseIds(idsParam);
        return findByIdsPreserveOrder(ids);
    }

    /**
     * Preserves request id order (including duplicate ids); omits missing profiles.
     */
    @Transactional(readOnly = true)
    public List<ProfileResponse> findByIdsPreserveOrder(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Set<Long> unique = Set.copyOf(ids);
        Map<Long, UserProfile> byId = repository.findByUserIdIn(unique).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, p -> p));
        return ids.stream()
                .map(byId::get)
                .filter(p -> p != null)
                .map(UserProfileService::toPublicResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfileResponse getPublicProfile(long userId) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toPublicResponse(profile);
    }

    @Transactional
    public ProfileResponse patchMe(long userId, PatchProfileRequest patch) {
        UserProfile profile = repository.findById(userId).orElseGet(() -> newProfile(userId));
        if (patch.nickname() != null) {
            profile.setNickname(patch.nickname());
        }
        repository.save(profile);
        return toPublicResponse(profile);
    }

    private static List<Long> parseIds(String idsParam) {
        if (idsParam.isBlank()) {
            return List.of();
        }
        try {
            return Arrays.stream(idsParam.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ids must be comma-separated integers", ex);
        }
    }

    private static UserProfile newProfile(long userId) {
        return new UserProfile(userId, "user-" + userId);
    }

    private static ProfileResponse toPublicResponse(UserProfile p) {
        return new ProfileResponse(p.getUserId(), p.getNickname());
    }
}
