package com.project.user_service.domain.entity;

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
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    private UUID id;

    @NotNull
    private String username;

    @NotNull
    private String bio;

    private String imgUrl;

    private String livingPlace;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<Users> following = new HashSet<>();
}


