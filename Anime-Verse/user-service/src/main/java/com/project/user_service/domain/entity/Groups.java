package com.project.user_service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.neo4j.core.schema.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Groups {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String groupName;

    @NotNull
    private String groupBio;

    @NotNull
    @Property("group_profile_img")
    private String groupProfileImg;

    private String backgroundImg;

    @Relationship(type = "LEADS", direction = Relationship.Direction.OUTGOING)
    private Users leader;

    @Relationship(type = "ADMIN", direction = Relationship.Direction.OUTGOING)
    private Set<Users> admins = new HashSet<>();

    @Relationship(type = "MEMBER", direction = Relationship.Direction.OUTGOING)
    private Set<Users> members = new HashSet<>();

    @Relationship(type = "BLOCKED", direction = Relationship.Direction.OUTGOING)
    private Set<Users> blocked = new HashSet<>();
}
