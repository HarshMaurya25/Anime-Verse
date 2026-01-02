package com.project.recommendation_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table()
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Content content;

    @Column(name = "is_liked")
    private Boolean like;

    @Column(name = "is_disliked")
    private Boolean dislike;

    @Column(name = "is_commented")
    private Boolean comment;

    @Column
    private LocalDateTime interactAt;

    private List<String> contentTag;

    // @PrePersist
    // private void createAt() {
    // if (interactAt == null) {
    // interactAt = LocalDateTime.now();
    // }
    // }

    // @PreUpdate
    // private void updateAt() {
    // interactAt = LocalDateTime.now();
    // }
}
