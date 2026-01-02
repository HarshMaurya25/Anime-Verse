package com.project.recommendation_service.config;

import com.project.recommendation_service.domain.entity.Content;
import com.project.recommendation_service.domain.entity.UsersInteraction;
import com.project.recommendation_service.domain.enums.Category;
import com.project.recommendation_service.domain.enums.Genre;
import com.project.recommendation_service.util.AdvancedRecommendationQueryRunner;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EntityManager entityManager;
    private final AdvancedRecommendationQueryRunner advancedQueryRunner;

    private static final Genre[] GENRES = Genre.values();
    private static final Category[] CATEGORIES = Category.values();

    private static final String[] TEST_USERNAMES = {
            "AnimeNinja", "MangaLover", "LightNovels", "GamingKing",
            "MovieBuff", "SeriesWatcher"
    };

    private static final Map<String, List<String>> GENRE_TAGS = createGenreTags();

    private static final Map<String, List<String>> CATEGORY_TAGS = createCategoryTags();

    private static Map<String, List<String>> createGenreTags() {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("ACTION",
                List.of("fights", "combat", "explosions", "weapons", "high stakes", "fast-paced", "chases", "battles"));
        tags.put("ADVENTURE", List.of("journey", "exploration", "quests", "travel", "discovery", "new lands",
                "treasure", "expedition"));
        tags.put("COMEDY", List.of("humor", "parody", "misunderstandings", "slapstick", "satire", "jokes", "gags"));
        tags.put("DRAMA", List.of("emotional", "character development", "relationships", "conflict",
                "personal struggles", "growth", "serious tone"));
        tags.put("FANTASY", List.of("magic", "mythical creatures", "kingdoms", "world-building", "swords", "legends",
                "fantasy races"));
        tags.put("SCI_FI",
                List.of("technology", "space", "time travel", "AI", "futuristic", "advanced tech", "science"));
        tags.put("HORROR",
                List.of("fear", "suspense", "monsters", "horror elements", "survival", "dark atmosphere", "tension"));
        tags.put("MYSTERY",
                List.of("investigation", "secrets", "clues", "detective", "twists", "hidden truth", "puzzles"));
        tags.put("THRILLER",
                List.of("tension", "suspense", "danger", "psychological stress", "plot twists", "high pressure"));
        tags.put("SUPERNATURAL",
                List.of("ghosts", "spirits", "curses", "paranormal", "otherworldly", "occult", "mystic forces"));
        tags.put("PSYCHOLOGICAL", List.of("mind games", "trauma", "obsession", "mental struggle",
                "unreliable narration", "inner conflict"));
        tags.put("SPORTS",
                List.of("competition", "teamwork", "training", "tournaments", "rivalry", "matches", "athletes"));
        tags.put("MUSIC",
                List.of("bands", "performances", "concerts", "singing", "instruments", "practice", "idol life"));
        tags.put("MECHA", List.of("robots", "pilots", "warfare", "technology", "giant machines", "mech battles"));
        tags.put("HISTORICAL",
                List.of("past eras", "real events", "war", "politics", "culture", "traditions", "history-based"));
        tags.put("MILITARY", List.of("army", "strategy", "combat", "missions", "hierarchy", "tactics", "soldiers"));
        tags.put("HAREM", List.of("multiple love interests", "romantic rivalry", "misunderstandings",
                "protagonist-centered", "jealousy", "romantic comedy"));
        tags.put("ISEKAI", List.of("reincarnation", "alternate world", "overpowered protagonist", "game mechanics",
                "fantasy setting", "leveling", "new life"));
        tags.put("MAGIC", List.of("spells", "mana", "enchantments", "magic schools", "rituals", "spellcasting"));
        tags.put("SCHOOL",
                List.of("students", "classrooms", "exams", "clubs", "coming-of-age", "school life", "friendships"));
        tags.put("DEMON", List.of("demons", "dark fantasy", "possession", "exorcism", "curses", "evil forces"));
        return tags;
    }

    private static Map<String, List<String>> createCategoryTags() {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("MEME", List.of("meme", "shitpost", "reaction", "template", "satire", "inside-joke", "format",
                "relatable", "low-effort"));
        tags.put("FUNNY", List.of("humor", "comedy", "parody", "jokes", "lighthearted", "skit", "gag", "laughs"));
        tags.put("DISCUSSION",
                List.of("discussion", "debate", "community", "thoughts", "opinions", "open-ended", "conversation"));
        tags.put("QUESTION", List.of("question", "help", "advice", "asking", "clarification", "support", "how-to"));
        tags.put("OPINION",
                List.of("opinion", "hot take", "personal view", "subjective", "thoughts", "take", "perspective"));
        tags.put("REVIEW", List.of("review", "rating", "critique", "recommendation", "pros", "cons", "verdict"));
        tags.put("ANALYSIS",
                List.of("analysis", "theory", "deep-dive", "breakdown", "interpretation", "symbolism", "lore"));
        tags.put("NEWS", List.of("news", "announcement", "update", "official", "confirmed", "information", "release"));
        tags.put("CLIP", List.of("clip", "highlight", "moment", "scene", "short", "snippet", "timestamp"));
        tags.put("FAN_ART",
                List.of("fanart", "drawing", "artwork", "illustration", "digital-art", "sketch", "creative"));
        tags.put("EDIT", List.of("edit", "amv", "montage", "video-edit", "effects", "sync", "transitions"));
        tags.put("COSPLAY", List.of("cosplay", "costume", "photoshoot", "handmade", "wig", "props", "convention"));
        tags.put("SLICE_OF_LIFE",
                List.of("daily life", "wholesome", "relatable", "chill", "real-life", "casual", "vibes"));
        tags.put("STORY", List.of("story", "fanfic", "writing", "narrative", "original-content", "chapter", "plot"));
        tags.put("ROMANCE", List.of("romance", "shipping", "couple", "love", "feelings", "relationship"));
        tags.put("ECCHI", List.of("fanservice", "suggestive", "comedic", "playful", "teasing"));
        tags.put("META", List.of("meta", "rules", "feedback", "announcement", "community-update", "moderation"));
        return tags;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long contentCount = entityManager.createQuery("SELECT COUNT(c) FROM Content c", Long.class)
                .getSingleResult();

        if (contentCount == 0) {
            System.out.println("Seeding 150 random content entries...");
            List<Content> contents = createRandomContent();
            System.out.println("Content seeding completed!");

            System.out.println("Assigning content to random users...");
            List<UUID> testUserIds = assignUsersToContent(contents);
            System.out.println("User assignment completed!");

            // Advanced recommendation query disabled - data saved to database
            System.out.println("\n✅ Data seeding complete! All data saved to database.");
        } else {
            // Backfill any missing interaction timestamps from older runs
            backfillInteractionTimestamps();

            // Run advanced recommendation query for first test user
            List<UUID> testUserIds = entityManager
                    .createQuery("SELECT DISTINCT ui.userId FROM UsersInteraction ui", UUID.class)
                    .setMaxResults(1)
                    .getResultList();

            if (!testUserIds.isEmpty()) {
                System.out.println("\nExecuting advanced recommendation analysis...");
                advancedQueryRunner.runAdvancedRecommendation(testUserIds.get(0).toString());
            }
        }
    }

    private List<Content> createRandomContent() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);
        List<Content> contentList = new ArrayList<>();

        for (int i = 0; i < 1500; i++) {
            Set<String> genres = new HashSet<>();
            int numGenres = random.nextInt(3) + 1; // 1-3 genres
            while (genres.size() < numGenres) {
                Genre genre = GENRES[random.nextInt(GENRES.length)];
                genres.add(genre.toString());
            }

            Set<String> categories = new HashSet<>();
            int numCategories = random.nextInt(3) + 1;
            while (categories.size() < numCategories) {
                Category category = CATEGORIES[random.nextInt(CATEGORIES.length)];
                categories.add(category.toString());
            }

            String title = String.join("-", genres) + " " + String.join("-", categories);

            // Truncated normal distribution for time allocation
            double meanDays = 30; // most dates around 30 days ago
            double sdDays = 30; // standard deviation

            // Convert mean/SD to milliseconds
            long nowMillis = java.time.Instant.now().toEpochMilli();
            long oneYearAgoMillis = oneYearAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            long meanMillis = nowMillis - (long) (meanDays * 24 * 3600 * 1000);
            long sdMillis = (long) (sdDays * 24 * 3600 * 1000);

            long randomMillis;

            // Use truncated normal to avoid going below oneYearAgo or beyond now
            do {
                double gaussian = random.nextGaussian(); // mean=0, sd=1
                randomMillis = meanMillis + (long) (gaussian * sdMillis);
            } while (randomMillis < oneYearAgoMillis || randomMillis > nowMillis);

            LocalDateTime randomDate = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(randomMillis),
                    java.time.ZoneOffset.UTC);

            Content content = Content.builder()
                    .contentId(UUID.randomUUID())
                    .contentTitle(title + " #" + (i + 1))
                    .genre(genres)
                    .category(categories)
                    .timeOfCreation(randomDate)
                    .likeCount(random.nextInt(100) + 1)
                    .dislikeCount(random.nextInt(100) + 1)
                    .commentCount(random.nextInt(40) + 1)
                    .contentTag(getRandomTags(genres, categories, random))
                    .enable(true)
                    .build();

            contentList.add(content);

            if ((i + 1) % 50 == 0) {
                System.out.println("Prepared " + (i + 1) + " content entries...");
            }
        }

        // Bulk persist all content at once
        System.out.println("Bulk persisting " + contentList.size() + " content entries...");
        for (Content content : contentList) {
            entityManager.persist(content);
        }
        entityManager.flush();
        entityManager.clear(); // Clear L1 cache to free memory

        return contentList;
    }

    private List<UUID> assignUsersToContent(List<Content> contents) {
        Random random = new Random();

        List<UUID> testUserIds = new ArrayList<>();
        for (String username : TEST_USERNAMES) {
            UUID userId = UUID.randomUUID();
            testUserIds.add(userId);
            System.out.println("Test User: " + username + " -> UUID: " + userId);
        }

        List<UsersInteraction> interactions = new ArrayList<>();
        int interactionCount = 0;

        for (Content content : contents) {
            int usersPerContent = random.nextInt(3) + 1;

            for (int i = 0; i < usersPerContent; i++) {
                UUID userId = testUserIds.get(random.nextInt(testUserIds.size()));

                // Random interaction time between content creation and now
                LocalDateTime createdAt = content.getTimeOfCreation();
                LocalDateTime now = LocalDateTime.now();
                long interactionMillis = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli() +
                        (long) (random.nextDouble() * (now.toInstant(ZoneOffset.UTC).toEpochMilli() -
                                createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()));
                LocalDateTime interactionDate = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(interactionMillis),
                        ZoneOffset.UTC);

                UsersInteraction interaction = UsersInteraction.builder()
                        .userId(userId)
                        .content(content)
                        .like(random.nextBoolean())
                        .dislike(random.nextBoolean())
                        .comment(random.nextBoolean())
                        .interactAt(interactionDate)
                        .build();

                interactions.add(interaction);
                interactionCount++;

                // Batch persist every 100 interactions
                if (interactionCount % 100 == 0) {
                    for (UsersInteraction ui : interactions) {
                        entityManager.persist(ui);
                    }
                    entityManager.flush();
                    entityManager.clear();
                    interactions.clear();
                    System.out.println("Persisted " + interactionCount + " interactions...");
                }
            }
        }

        // Persist remaining interactions
        if (!interactions.isEmpty()) {
            for (UsersInteraction ui : interactions) {
                entityManager.persist(ui);
            }
            entityManager.flush();
            entityManager.clear();
        }

        System.out.println("Assigned " + TEST_USERNAMES.length + " test users to content with " + interactionCount
                + " total interactions!");
        return testUserIds;
    }

    /**
     * For existing interactions with null interactAt (created before the field was
     * set),
     * fill with a reasonable timestamp between the content creation time and now.
     */
    private void backfillInteractionTimestamps() {
        List<UsersInteraction> nullInteractions = entityManager
                .createQuery("SELECT ui FROM UsersInteraction ui WHERE ui.interactAt IS NULL", UsersInteraction.class)
                .getResultList();

        if (nullInteractions.isEmpty()) {
            return;
        }

        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        for (UsersInteraction ui : nullInteractions) {
            LocalDateTime createdAt = ui.getContent() != null && ui.getContent().getTimeOfCreation() != null
                    ? ui.getContent().getTimeOfCreation()
                    : now.minusMonths(3); // fallback window if content timestamp missing

            long interactionMillis = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli() +
                    (long) (random.nextDouble() * (now.toInstant(ZoneOffset.UTC).toEpochMilli() -
                            createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()));

            LocalDateTime interactionDate = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(interactionMillis),
                    ZoneOffset.UTC);

            ui.setInteractAt(interactionDate);
        }

        entityManager.flush();
        System.out.println("Backfilled interactAt for " + nullInteractions.size() + " interactions.");
    }

    /**
     * Generate random tags: 1-2 from genre and 1-3 from category
     */
    private Set<String> getRandomTags(Set<String> genres, Set<String> categories, Random random) {
        Set<String> tags = new HashSet<>();

        // Add 1-2 random genre tags
        int genreTagCount = random.nextInt(2) + 1; // 1-2
        for (String genre : genres) {
            if (GENRE_TAGS.containsKey(genre)) {
                List<String> genreTags = GENRE_TAGS.get(genre);
                for (int i = 0; i < genreTagCount && tags.size() < genreTagCount * genres.size(); i++) {
                    tags.add(genreTags.get(random.nextInt(genreTags.size())));
                }
            }
        }

        // Add 1-3 random category tags
        int categoryTagCount = random.nextInt(3) + 1; // 1-3
        for (String category : categories) {
            if (CATEGORY_TAGS.containsKey(category)) {
                List<String> catTags = CATEGORY_TAGS.get(category);
                for (int i = 0; i < categoryTagCount && tags.size() < categoryTagCount * categories.size(); i++) {
                    tags.add(catTags.get(random.nextInt(catTags.size())));
                }
            }
        }

        return tags;
    }
}