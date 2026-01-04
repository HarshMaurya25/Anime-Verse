package com.project.user_service.repository;

import com.project.user_service.domain.entity.groups.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.users.id = :userId")
    Optional<GroupMember> findByGroupIdAndUserId(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id = :groupId")
    long countByGroupId(@Param("groupId") UUID groupId);

    @Query("""
        SELECT gm.group.id FROM GroupMember gm 
        WHERE gm.users.id IN :userIds
    """)
    Set<UUID> getUserGroups(@Param("userIds") Set<UUID> userIds);

    @Query("SELECT gm.group.id FROM GroupMember gm WHERE gm.users.id = :id")
    Set<UUID> getUserGroup(@Param("id") UUID userId);

    @Query("SELECT gm.users.id FROM GroupMember gm WHERE gm.group.id = :groupId")
    Set<UUID> getAllMemberOfGroup(@Param("groupId") UUID groupID);

}
