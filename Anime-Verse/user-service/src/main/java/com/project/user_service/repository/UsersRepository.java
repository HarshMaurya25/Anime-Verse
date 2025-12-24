package com.project.user_service.repository;

import com.project.user_service.domain.dto.response.UserProfileResponseDto;
import com.project.user_service.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.followers LEFT JOIN FETCH u.following WHERE u.id = :id")
    Optional<Users> findByIdWithFollowers(@Param("id") UUID id);

    boolean existsById(UUID id);

    Users findByIdAndEnableTrue(UUID id);


    @Query("""
        SELECT u FROM Users u
        LEFT JOIN FETCH u.followers
        LEFT JOIN FETCH u.following
        WHERE u.id = :id
    """)
    Optional<Users> findByIdWithFollowersAndFollowing(UUID id);

}
