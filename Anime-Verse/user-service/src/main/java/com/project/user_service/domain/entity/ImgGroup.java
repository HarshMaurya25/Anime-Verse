package com.project.user_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImgGroup {

    @Id
    private UUID id;



    @Lob
    private byte[] profileImg;

    @NonNull
    private String imageType;
}
