package com.project.user_service.service;

import com.project.user_service.domain.dto.request.CreateUserDetailsRequestDto;
import com.project.user_service.domain.dto.request.UpdateUserProfileRequestDto;
import com.project.user_service.domain.dto.response.UserProfileResponseDto;
import com.project.user_service.domain.entity.Users;
import com.project.user_service.exception.customException.ImageUploadFailedException;
import com.project.user_service.exception.customException.UserAlreadyExistsException;
import com.project.user_service.exception.customException.UserNotFoundException;
import com.project.user_service.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UsersRepository userRepository;

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final int PAGE_LIMIT = 30;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

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
    public UserProfileResponseDto createUser(CreateUserDetailsRequestDto requestDto , MultipartFile file){

        if(userRepository.existsById(requestDto.getId())){
            throw new UserAlreadyExistsException(requestDto.getUsername());
        }

        byte[] image = null;
        String imageType = null;

        if (file != null && !file.isEmpty()) {

            validateProfileImage(file);

            try {
                image = file.getBytes();
                imageType = file.getContentType();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read profile image", e);
            }
        }

        Users user = Users
                .builder()
                .id(requestDto.getId())
                .username(requestDto.getUsername())
                .bio(requestDto.getBio())
                .location(requestDto.getLocation())
                .displayName(requestDto.getDisplayName())
                .dateOfBirth(requestDto.getDateOfBirth())
                .profileImage(image)
                .imageType(imageType)
                .build();

        userRepository.save(user);

        log.info("User {} created with id {}", user.getUsername(), user.getId());

        return UserProfileResponseDto
                .builder()
                .id(requestDto.getId())
                .displayName(requestDto.getDisplayName())
                .username(requestDto.getUsername())
                .bio(user.getBio())
                .location(user.getLocation())
                .profileImg(user.getProfileImage())
                .imageType(user.getImageType())
                .isVerified(false)
                .followers(0)
                .following(0)
                .build();
    }

    @Transactional
    public UserProfileResponseDto getProfile(UUID id) {
        Users user = userRepository.findByIdWithFollowers(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        if(!user.isEnable()){
            throw new UserNotFoundException(id.toString());
        }

        return UserProfileResponseDto.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .username(user.getUsername())
                .bio(user.getBio())
                .followers(user.getFollowers().size())
                .following(user.getFollowing().size())
                .location(user.getLocation())
                .profileImg(user.getProfileImage())
                .imageType(user.getImageType())
                .isVerified(user.isVerified())
                .build();
    }

    @Transactional
    public boolean uploadImage(UUID id , MultipartFile file){
        if(id == null || file == null || file.isEmpty()){
            throw new IllegalArgumentException("Id or Image");
        }

        validateProfileImage(file);

        Users user = userRepository.findByIdAndEnableTrue(id);

        if(user == null){
            throw new UserNotFoundException(id.toString());
        }

        try {
            user.setProfileImage(file.getBytes());
            user.setImageType(file.getContentType());

            userRepository.save(user);
        } catch (IOException e) {
            throw new ImageUploadFailedException(e.getMessage());
        }

        log.info("User : {} with username : {} update Profile Image" , user.getId().toString() , user.getUsername());
        return true;
    }

    @Transactional
    public UserProfileResponseDto updateUserProfile(
            UUID id,
            UpdateUserProfileRequestDto dto
    ) {

        if(id == null){
            throw new IllegalArgumentException("User id must not be null");
        }

        List<String> updated = new ArrayList<>();

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        if (!user.isEnable()) {
            throw new UserNotFoundException(id.toString());
        }

        if (dto.getDisplayName() != null) {
            user.setDisplayName(dto.getDisplayName());
            updated.add("Display Name");
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
            updated.add("Bio");
        }

        if (dto.getLocation() != null) {
            user.setLocation(dto.getLocation());
            updated.add("Location");
        }

        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
            updated.add("Date of Birth");
        }

        userRepository.save(user);

        log.info("User : {} ({}) updated its : {}" , user.getId().toString() , user.getUsername() , updated.toString());

        return UserProfileResponseDto.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .username(user.getUsername())
                .bio(user.getBio())
                .location(user.getLocation())
                .followers(user.getFollowers().size())
                .following(user.getFollowing().size())
                .profileImg(user.getProfileImage())
                .imageType(user.getImageType())
                .isVerified(user.isVerified())
                .build();
    }

    @Transactional
    public void followUser(UUID userId, UUID targetUserId) {

        if(userId == null || targetUserId == null){
            throw new IllegalArgumentException("Id must not be null");
        }

        if (userId.equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        Users user = userRepository.findByIdWithFollowersAndFollowing(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Users targetUser = userRepository.findByIdWithFollowersAndFollowing(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId.toString()));

        if (!user.isEnable() || !targetUser.isEnable()) {
            throw new UserNotFoundException("User is disabled");
        }

        if (targetUser.getFollowers().contains(user)) {
            throw new IllegalStateException("Already following this user");
        }

        targetUser.getFollowers().add(user);
        user.getFollowing().add(targetUser);

        userRepository.save(user);
        userRepository.save(targetUser);


        log.info("User followed successfully: userId={} targetUserId={}",
                userId, targetUserId);
    }

    @Transactional
    public void unfollowUser(UUID userId, UUID targetUserId) {

        if(userId == null || targetUserId == null){
            throw new IllegalArgumentException("Id must not be null");
        }

        if (userId.equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot unfollow yourself");
        }

        Users user = userRepository.findByIdWithFollowersAndFollowing(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Users targetUser = userRepository.findByIdWithFollowersAndFollowing(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId.toString()));

        if (!targetUser.getFollowers().contains(user)) {
            throw new IllegalStateException("You are not following this user");
        }

        targetUser.getFollowers().remove(user);
        user.getFollowing().remove(targetUser);

        userRepository.save(user);
        userRepository.save(targetUser);

        log.info("User unfollowed successfully: userId={} targetUserId={}",
                userId, targetUserId);
    }



}

