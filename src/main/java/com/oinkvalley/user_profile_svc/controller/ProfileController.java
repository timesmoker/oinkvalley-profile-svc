package com.oinkvalley.user_profile_svc.controller;

import com.oinkvalley.user_profile_svc.service.UserProfileService;
import com.oinkvalley.user_profile_svc.dto.profile.ProfileResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final UserProfileService userProfileService;

    public ProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    /**
     * 보드 등에서 모은 {@code userId} 목록으로 닉네임까지 한 번에 조회 (순서·중복 id 유지, 없는 id 생략).
     */
    @GetMapping(params = "ids")
    public List<ProfileResponse> batchByIds(@RequestParam("ids") String ids) {
        return userProfileService.batchByIdsParam(ids);
    }

    @GetMapping("/{userId}")
    public ProfileResponse getProfile(@PathVariable long userId) {
        return userProfileService.getPublicProfile(userId);
    }

}