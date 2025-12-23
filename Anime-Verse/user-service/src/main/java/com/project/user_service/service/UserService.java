package com.project.user_service.service;

import com.project.user_service.domain.dto.request.CreateUserDetailsRequestDto;
import com.project.user_service.domain.dto.response.UserProfileResponseDto;
import com.project.user_service.domain.entity.Users;
import com.project.user_service.domain.neo4j.UserGraphs;
import com.project.user_service.exception.customException.UserNotFoundException;
import com.project.user_service.repository.GraphUserRepo;
import com.project.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GraphUserRepo graphUserRepo;

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );


    private void validateProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile image is required");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Profile image must be less than 10 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, or WEBP images are allowed");
        }
    }


    @Transactional
    public UserProfileResponseDto createUser(
            CreateUserDetailsRequestDto requestDto,
            MultipartFile profileImageFile
    ) {

        byte[] image = null;
        String imageType = null;

        if (profileImageFile != null && !profileImageFile.isEmpty()) {

            validateProfileImage(profileImageFile);

            try {
                image = profileImageFile.getBytes();
                imageType = profileImageFile.getContentType();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read profile image", e);
            }
        }

        Users user = Users.builder()
                .id(requestDto.getId())
                .username(requestDto.getUsername())
                .bio(requestDto.getBio())
                .dateOfBirth(requestDto.getDateOfBirth())
                .country(requestDto.getCountry())
                .profileImg(image)
                .imageType(imageType)
                .build();

        userRepository.save(user);

        try {
            graphUserRepo.save(
                    UserGraphs.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .build()
            );
        } catch (Exception e) {
            userRepository.delete(user);
            log.error("Graph user creation failed for user {}", user.getId(), e);
        }

        log.info("User {} created with id {}", user.getUsername(), user.getId());

        return UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .country(user.getCountry())
                .followers(0)
                .following(0)
                .build();
    }

    public UserProfileResponseDto getUserDetail(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        byte[] image = user.getProfileImg();
        String imageType = user.getImageType();

        long followers = 0;
        long following = 0;

        try {
            Map<String, Long> count =
                    graphUserRepo.countFollowingAndFollowers(user.getId());

            followers = count.getOrDefault("followersCount", 0L);
            following = count.getOrDefault("followingCount", 0L);

        } catch (Exception e) {
            log.warn("Failed to fetch follow counts for user {}", user.getId());
        }

        return UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .country(user.getCountry())
                .followers(followers)
                .following(following)
                .profileImg(image)
                .imageType(imageType)
                .build();
    }

    @Transactional
    public void profileImageUpload(MultipartFile profileImage, UUID id) {

        if (profileImage == null || profileImage.isEmpty()) {
            throw new IllegalArgumentException("Profile image must not be empty");
        }

        validateProfileImage(profileImage);

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        try {
            user.setProfileImg(profileImage.getBytes());
            user.setImageType(profileImage.getContentType());
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image bytes", e);
        }

        log.info("User {} updated profile image", user.getId());
    }

    @Transactional
    public void followSomeone(UUID userId, UUID toFollow) {

        Optional<Map<String, Object>> result =
                graphUserRepo.followUserByIdSafe(userId, toFollow);

        if (result.isEmpty()) {
            throw new UserNotFoundException(
                    "Follower or followee does not exist"
            );
        }

        log.info("User {} followed user {}", userId, toFollow);
    }
}

