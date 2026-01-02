package com.project.recommendation_service.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdvancedRecommendationQueryRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void runAdvancedRecommendation(String userId) {
        String sql = """
                WITH
                -- 0) PARAMETERS
                params AS (
                    SELECT
                        CAST(:userId AS uuid) AS user_id,
                        30.0 AS half_life_days
                ),

                -- 1) TIME-DECAYED INTERACTION SCORES
                interaction_scores AS (
                    SELECT
                        ui.content_content_id,
                        ui.interact_at,
                        EXTRACT(DAY FROM (NOW() - ui.interact_at)) AS days_ago,
                        EXP(-LN(2) * EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 14.0) AS time_decay,
                        CASE
                            WHEN ui.is_liked THEN 5
                            WHEN ui.is_commented THEN 3
                            WHEN ui.is_disliked THEN -3
                            ELSE 1
                        END AS base_score,
                        CASE
                            WHEN ui.is_liked THEN 5 * EXP(-LN(2) * EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 14.0)
                            WHEN ui.is_commented THEN 3 * EXP(-LN(2) * EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 14.0)
                            WHEN ui.is_disliked THEN -3 * EXP(-LN(2) * EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 14.0)
                            ELSE 1 * EXP(-LN(2) * EXTRACT(DAY FROM (NOW() - ui.interact_at)) / 14.0)
                        END AS score
                    FROM users_interaction ui
                    JOIN params p ON ui.user_id = p.user_id
                    WHERE ui.interact_at IS NOT NULL
                ),

                -- GENRE NORMALIZATION
                genre_stats AS (
                    SELECT
                        cg.genre,
                        AVG(isa.score) AS raw_score_genre
                    FROM interaction_scores isa
                    JOIN content c ON c.content_id = isa.content_content_id
                    JOIN content_genres cg ON cg.content_content_id = c.content_id
                    GROUP BY cg.genre
                ),

                genre_normalized AS (
                    SELECT
                        genre,
                        raw_score_genre,
                        raw_score_genre / (SUM(raw_score_genre) OVER () + 0.0001) AS affinity_weight_genre
                    FROM genre_stats
                ),

                -- CATEGORY NORMALIZATION
                category_stats AS (
                    SELECT
                        cc.category,
                        AVG(isa.score) AS raw_score_category
                    FROM interaction_scores isa
                    JOIN content c ON c.content_id = isa.content_content_id
                    JOIN content_categories cc ON cc.content_content_id = c.content_id
                    GROUP BY cc.category
                ),

                category_normalized AS (
                    SELECT
                        category,
                        raw_score_category,
                        raw_score_category / (SUM(raw_score_category) OVER () + 0.0001) AS affinity_weight_category
                    FROM category_stats
                ),

                -- TAG NORMALIZATION
                tag_stats AS (
                    SELECT
                        ct.content_tag AS tag,
                        AVG(isa.score) AS raw_score_tag
                    FROM interaction_scores isa
                    JOIN content c ON c.content_id = isa.content_content_id
                    JOIN content_tags ct ON ct.content_content_id = c.content_id
                    GROUP BY ct.content_tag
                ),

                tag_normalized AS (
                    SELECT
                        tag,
                        raw_score_tag,
                        raw_score_tag / (SUM(raw_score_tag) OVER () + 0.0001) AS affinity_weight_tag
                    FROM tag_stats
                ),

                -- 2) CANDIDATE SELECTION & VETO LOGIC
                candidate_content AS (
                    SELECT DISTINCT
                        c.content_id,
                        c.content_title,
                        c.time_of_creation,
                        c.like_count,
                        c.comment_count,
                        c.dislike_count,
                        EXTRACT(DAY FROM (NOW() - c.time_of_creation))::int AS days_ago,

                        COALESCE(MAX(gn.affinity_weight_genre), 0) AS w_genre,
                        COALESCE(MAX(cn.affinity_weight_category), 0) AS w_category,
                        COALESCE(MAX(tn.affinity_weight_tag), 0) AS w_tag,

                        CASE
                            WHEN MAX(CASE WHEN cc.category = 'CLIP' THEN 1 ELSE 0 END) = 1 THEN 1
                            WHEN MAX(CASE WHEN cc.category IN ('PODCAST', 'INDUSTRY') THEN 1 ELSE 0 END) = 1 THEN 1
                            ELSE 0
                        END AS is_vetoed

                    FROM content c
                    JOIN content_categories cc ON cc.content_content_id = c.content_id
                    JOIN content_genres cg ON cg.content_content_id = c.content_id
                    LEFT JOIN genre_normalized gn ON gn.genre = cg.genre
                    LEFT JOIN category_normalized cn ON cn.category = cc.category
                    LEFT JOIN content_tags ct ON ct.content_content_id = c.content_id
                    LEFT JOIN tag_normalized tn ON tn.tag = ct.content_tag
                    CROSS JOIN params p
                    WHERE
                        NOT EXISTS (SELECT 1 FROM users_interaction ui WHERE ui.content_content_id = c.content_id AND ui.user_id = p.user_id)
                    GROUP BY c.content_id, c.content_title, c.time_of_creation, c.like_count, c.comment_count, c.dislike_count
                ),

                -- 3) SCORING & RANKING
                scored_content AS (
                    SELECT
                        content_id,
                        content_title,
                        time_of_creation,
                        days_ago,
                        like_count,
                        comment_count,
                        dislike_count,
                        w_genre,
                        w_category,
                        w_tag,

                        -- Quality Score (engagement ratio)
                        (like_count + comment_count + 1.0) / (like_count + comment_count + dislike_count + 3.0) AS quality_score,

                        -- Freshness Score (30-day half-life)
                        EXP(-LN(2) * days_ago / 30.0) AS freshness_score,

                        -- Affinity Score (weighted sum of normalized preferences)
                        1.0 + 2.0 * w_genre + 3.0 * w_category + 1.5 * w_tag AS affinity_score,

                        -- Final Score
                        (0.6 * (like_count + comment_count + 1.0) / (like_count + comment_count + dislike_count + 3.0))
                        * (1.0 + 2.0 * w_genre + 3.0 * w_category + 1.5 * w_tag)
                        * (0.4 + 0.6 * EXP(-LN(2) * days_ago / 30.0)) AS final_score

                    FROM candidate_content
                    WHERE is_vetoed = 0
                )

                SELECT
                    content_id,
                    content_title,
                    days_ago,
                    ROUND(quality_score::numeric, 3) AS quality,
                    ROUND(freshness_score::numeric, 3) AS freshness,
                    ROUND(affinity_score::numeric, 3) AS affinity,
                    ROUND(final_score::numeric, 3) AS score,
                    RANK() OVER (ORDER BY final_score DESC) AS satisfaction_rank
                FROM scored_content
                ORDER BY final_score DESC
                LIMIT 50
                """;

        try {
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .getResultList();

            System.out.println(
                    "\n╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                    🎯 ADVANCED RECOMMENDATION RESULTS FOR USER: "
                    + userId + "                                    ║");
            System.out.println(
                    "╠════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣");
            System.out.println(
                    "║ Rank │ Title                                      │ Age(d) │ Quality │ Fresh │ Affinity │  Score  ║");
            System.out.println(
                    "╠════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣");

            for (Object[] row : results) {
                String contentId = row[0].toString();
                String title = row[1].toString();
                if (title.length() > 40)
                    title = title.substring(0, 37) + "...";
                Integer daysAgo = ((Number) row[2]).intValue();
                Double quality = ((Number) row[3]).doubleValue();
                Double freshness = ((Number) row[4]).doubleValue();
                Double affinity = ((Number) row[5]).doubleValue();
                Double score = ((Number) row[6]).doubleValue();
                Integer rank = ((Number) row[7]).intValue();

                System.out.printf("║ %4d │ %-42s │ %6d │  %5.3f  │ %5.3f │   %5.3f  │ %7.3f ║%n",
                        rank, title, daysAgo, quality, freshness, affinity, score);
            }

            System.out.println(
                    "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println("Total recommendations: " + results.size());

        } catch (Exception e) {
            System.err.println("Error running advanced recommendation query: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
