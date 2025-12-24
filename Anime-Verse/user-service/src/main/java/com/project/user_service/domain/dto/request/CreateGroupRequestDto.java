package com.project.user_service.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequestDto {

    @NotBlank
    private String groupName;

    @NotBlank
    private String groupBio;

    @NonNull
    UUID leaderId;

}
