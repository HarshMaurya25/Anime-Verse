package com.project.user_service.domain.dto.internal;

import com.project.user_service.domain.neo4j.UserGraphs;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGraphsDTO {
    private UserGraphs user;
    private List<UserGraphs> following;
    private List<UserGraphs> followers;
}
