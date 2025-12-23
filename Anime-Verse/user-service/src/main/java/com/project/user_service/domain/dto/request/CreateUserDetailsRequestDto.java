package com.project.user_service.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDetailsRequestDto {

    @NotNull
    private UUID id;

    @Length(min = 6, max = 50, message = "Username length must be between 6 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, digits, and underscores"
    )
    private String username;

    private String bio;

    @NotBlank
    private String country;

    @NotBlank
    private Date dateOfBirth;
}
