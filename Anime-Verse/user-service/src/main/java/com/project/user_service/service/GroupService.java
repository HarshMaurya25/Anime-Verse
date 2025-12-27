package com.project.user_service.service;

import com.project.user_service.domain.dto.request.CreateGroupRequestDto;
import com.project.user_service.domain.dto.response.GroupResponseDto;
import com.project.user_service.domain.entity.groups.Group;
import com.project.user_service.domain.entity.groups.ImageGroup;
import com.project.user_service.domain.entity.users.Users;
import com.project.user_service.domain.enums.RedisMethod;
import com.project.user_service.exception.customException.ImageUploadFailedException;
import com.project.user_service.exception.customException.UserNotFoundException;
import com.project.user_service.repository.GroupRepository;
import com.project.user_service.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UsersRepository usersRepository;
    private final RedisService redisService;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final int PAGE_LIMIT = 10;
    private static final long TIME_REDIS = 600L;
    private static final long TIME_REDIS_MAX = 36000 * 5;

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
    public GroupResponseDto createGroup(CreateGroupRequestDto requestDto,
                                        MultipartFile profileImage,
                                        MultipartFile bgImage) {

        Users user = usersRepository.findByIdAndEnableTrue(requestDto.getLeaderId());
        if(user == null) {
            throw new UserNotFoundException(requestDto.getLeaderId().toString());
        }

        Group group = Group.builder()
                .groupName(requestDto.getGroupName())
                .bio(requestDto.getGroupBio())
                .leader(user)
                .build();

        byte[] profileImageBytes = null;
        byte[] bgImageBytes = null;
        String profileImageType = null;
        String bgImageType = null;

        try {
            if(profileImage != null && !profileImage.isEmpty()) {
                validateProfileImage(profileImage);
                profileImageBytes = profileImage.getBytes();
                profileImageType = profileImage.getContentType();
            }
        } catch (IOException e) {
            log.error("Image Upload of profile For Group Fail : {}" ,e.getMessage());
            throw new ImageUploadFailedException("Failed to profile images");
        }

        try{
            if(bgImage != null && !bgImage.isEmpty()) {
                validateProfileImage(bgImage);
                bgImageBytes = bgImage.getBytes();
                bgImageType = bgImage.getContentType();
            }
        } catch (IOException e) {
            log.error("Image Upload of Background For Group Fail : {}" ,e.getMessage());
            throw new ImageUploadFailedException("Failed to background images");
        }

        ImageGroup imageGroup = ImageGroup.builder()
                .profileImage(profileImageBytes)
                .profileImageType(profileImageType)
                .bgImage(bgImageBytes)
                .bgImageType(bgImageType)
                .build();

        group.setImages(imageGroup);
        groupRepository.save(group);

        user.getLeaderOfGroup().add(group);

        log.info("Group is created by Id : {} and groupName : {} by leader : {} ({})" ,
                group.getId().toString() , group.getGroupName() , user.getId().toString() , user.getUsername()
                );

        GroupResponseDto responseDto =  GroupResponseDto
                .builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .groupBio(group.getBio())
                .dateOfCreation(group.getDateOfCreation())
                .memberCount(0)
                .leaderUsername(user.getUsername())
                .leaderDisplayName(user.getDisplayName())
                .leaderId(user.getId())
                .profileImage(imageGroup.getProfileImage())
                .profileImageType(imageGroup.getProfileImageType())
                .bgImage(imageGroup.getBgImage())
                .bgImageType(imageGroup.getBgImageType())
                .build();

        redisService.set(RedisMethod.GROUP_ + group.getId().toString() , responseDto , TIME_REDIS);
        return responseDto;
    }


}
