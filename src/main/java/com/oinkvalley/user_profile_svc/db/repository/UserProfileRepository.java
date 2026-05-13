package com.oinkvalley.user_profile_svc.db.repository;

import com.oinkvalley.user_profile_svc.db.domain.UserProfile;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    List<UserProfile> findByUserIdIn(Collection<Long> userIds);
}
