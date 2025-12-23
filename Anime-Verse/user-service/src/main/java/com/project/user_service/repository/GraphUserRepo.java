package com.project.user_service.repository;

import com.project.user_service.domain.dto.internal.UserGraphsDTO;
import com.project.user_service.domain.neo4j.UserGraphs;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GraphUserRepo extends Neo4jRepository<UserGraphs , UUID> {

    @Query("MATCH (u:UserGraphs {username: $username}) " +
            "OPTIONAL MATCH (u)-[:FOLLOWS]->(f:UserGraphs) " +
            "OPTIONAL MATCH (u)<-[:FOLLOWS]-(fo:UserGraphs) " +
            "RETURN u, collect(f) as following, collect(fo) as followers")
    UserGraphsDTO findUserGraphDTO(String username);


    @Query("""
        MATCH (u:UserGraphs {id: $id})
        OPTIONAL MATCH (u)-[:FOLLOWS]->(f:UserGraphs)
        OPTIONAL MATCH (u)<-[:FOLLOWS]-(fo:UserGraphs)
        RETURN count(f) AS followingCount, count(fo) AS followersCount
        """)
    Map<String, Long> countFollowingAndFollowers(UUID id);

    @Query("""
        MATCH (u:UserGraphs {username: $followerUsername})
        MATCH (v:UserGraphs {username: $followeeUsername})
        MERGE (u)-[:FOLLOWS]->(v)
        """)
    void followUserByUsername(String followerUsername, String followeeUsername);

    @Query("""
        MATCH (follower:UserGraphs {id: $followerId})
        MATCH (followee:UserGraphs {id: $followeeId})
        MERGE (follower)-[:FOLLOWS]->(followee)
        RETURN follower, followee
    """)
    Optional<Map<String, Object>> followUserByIdSafe(UUID followerId, UUID followeeId);

}
