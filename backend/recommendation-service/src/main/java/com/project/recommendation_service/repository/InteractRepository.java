package com.project.recommendation_service.repository;

import com.project.recommendation_service.domain.dto.CategoryAffinityProjection;
import com.project.recommendation_service.domain.dto.GenreAffinityProjection;
import com.project.recommendation_service.domain.entity.UsersInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InteractRepository extends JpaRepository<UsersInteraction , UUID> {

    @Query(
            "SELECT i FROM UsersInteraction i WHERE i.userId = :userId AND i.content.contentId = :contentId"
    )
    UsersInteraction findByUserIdAndContent(
            @Param("userId") UUID userId,
            @Param("contentId") UUID contentId);
}
