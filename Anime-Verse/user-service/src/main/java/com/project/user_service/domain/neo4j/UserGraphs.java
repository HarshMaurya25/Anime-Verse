package com.project.user_service.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.neo4j.core.schema.*;
import lombok.*;
import java.util.*;

@Node
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGraphs {

    @Id
    UUID id;

    @NotNull
    private String username;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<UserGraphs> following = new HashSet<>();
}


