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
            value = """
        WITH interaction_scores AS (
            SELECT
                cg.genre,
                (
                    CASE
                        WHEN ui.is_liked THEN 1.0
                        WHEN ui.is_commented THEN 3.5
                        WHEN ui.is_disliked THEN -3.0
                        ELSE 0
                    END
                )
                *
                EXP(
                    -LN(2) *
                    EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 21.0
                ) AS decayed_score
            FROM users_interaction ui
            JOIN content_genres cg
              ON cg.content_id = ui.content_content_id
            WHERE ui.user_id = :userId
        ),
        genre_stats AS (
            SELECT
                genre,
                SUM(decayed_score) AS raw_score
            FROM interaction_scores
            GROUP BY genre
        ),
        genre_normalized AS (
            SELECT
                genre,
                raw_score,
                raw_score / (SUM(raw_score) OVER () + 0.0001) AS affinity_weight
            FROM genre_stats
        )
        SELECT
            genre        AS genre,
            raw_score    AS rawScore,
            affinity_weight AS affinityWeight
        FROM genre_normalized
        WHERE raw_score > 0
        ORDER BY affinity_weight DESC
        """,
            nativeQuery = true
    )
    List<GenreAffinityProjection> findUserGenreAffinity(
            @Param("userId") UUID userId
    );

    @Query(
            value = """
        WITH interaction_scores AS (
            SELECT
                cc.category,
                (
                    CASE
                        WHEN ui.is_liked THEN 1.0
                        WHEN ui.is_commented THEN 3.5
                        WHEN ui.is_disliked THEN -3.0
                        ELSE 0
                    END
                )
                *
                EXP(
                    -LN(2) *
                    EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 21.0
                ) AS decayed_score
            FROM users_interaction ui
            JOIN content_categories cc
              ON cc.content_id = ui.content_content_id
            WHERE ui.user_id = :userId
        ),
        category_stats AS (
            SELECT
                category,
                SUM(decayed_score) AS raw_score
            FROM interaction_scores
            GROUP BY category
        ),
        category_normalized AS (
            SELECT
                category,
                raw_score,
                raw_score / (SUM(raw_score) OVER () + 0.0001) AS affinity_weight
            FROM category_stats
        )
        SELECT
            category           AS category,
            raw_score          AS rawScore,
            affinity_weight    AS affinityWeight
        FROM category_normalized
        WHERE raw_score > 0
        ORDER BY affinity_weight DESC
        """,
            nativeQuery = true
    )
    List<CategoryAffinityProjection> findUserCategoryAffinity(
            @Param("userId") UUID userId
    );

}
