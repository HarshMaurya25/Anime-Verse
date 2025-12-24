package com.project.user_service.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDetailsRequestDto {

    @NotNull
    private UUID id;

    @NotBlank
    @Length(min = 6, max = 50, message = "Username length must be between 6 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, digits, and underscores"
    )
    private String username;

    @NotBlank
    @Length(min = 6, max = 50, message = "Username length must be between 6 and 50 characters")
    private String displayName;

    @NotBlank
    private String bio;

    @NotBlank
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
}
