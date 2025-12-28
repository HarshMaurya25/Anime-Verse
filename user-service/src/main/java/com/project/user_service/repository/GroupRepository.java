package com.project.user_service.repository;

import com.project.user_service.domain.dto.response.GroupResponseDto;
import com.project.user_service.domain.entity.groups.Group;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group , UUID> {

    @Query("SELECT new com.project.user_service.domain.dto.response.GroupResponseDto(" +
            "g.id, " +
            "g.groupName, " +
            "g.bio, " +
            "g.leader.username, " +
            "g.leader.id, " +
            "g.leader.displayName, " +
            "SIZE(g.members), " +
            "g.dateOfCreation, " +
            "null, null, null, null) " +
            "FROM Group g WHERE g.id = :id AND g.enable = true")
    Optional<GroupResponseDto> getGroupById(@Param("id") UUID id);

}
