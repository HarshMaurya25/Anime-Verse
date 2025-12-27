package com.project.user_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.action.internal.OrphanRemovalAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 50, nullable = false)
    private String displayName;

    @Column(nullable = false, length = 200)
    private String bio;

    @Column(nullable = false, length = 50)
    private String location;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "following")
    private Set<Follow> followers = new HashSet<>();

    // following = people I follow
    @OneToMany(mappedBy = "follower")
    private Set<Follow> following = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    private ImageUserEntity imageUserEntity;

    @Column(nullable = false)
    private boolean enable;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        // ensure the not-null updated_at column is initialized on first insert
        this.updatedAt = LocalDateTime.now();
    }
}
