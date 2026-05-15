package com.oinkvalley.user_profile_svc.controller;

import com.oinkvalley.user_profile_svc.dto.profile.CreateUserProfileRequest;
import com.oinkvalley.user_profile_svc.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** auth 등 클러스터 내부 서비스 전용. ingress 에 노출하지 않는다. */
@RestController
@RequestMapping("/internal/profiles")
public class InternalProfileController {

    private final UserProfileService userProfileService;

    public InternalProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProfile(@RequestBody @Valid CreateUserProfileRequest request) {
        userProfileService.createProfile(request);
    }

    @GetMapping("/exists")
    public boolean existsNickname(@RequestParam String nickname) {
        return userProfileService.existsNickname(nickname);
    }
}
