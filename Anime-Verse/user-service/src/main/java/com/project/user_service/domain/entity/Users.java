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

    @Column(nullable = false , unique = true)
    private String username;

    @Column(length = 50 , nullable = false)
    private String displayName;

    @Column(nullable = false , length = 200)
    private String bio;

    @Column(nullable = false , length = 50)
    private String location;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<Users> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<Users> following = new HashSet<>();

    @Lob
    private byte[] profileImage;

    private String imageType;

    @Column(nullable = false)
    private boolean enable = true;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
