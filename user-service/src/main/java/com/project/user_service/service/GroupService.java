package com.project.user_service.service;

import com.project.user_service.domain.dto.request.BioUpdateGroupRequestDto;
import com.project.user_service.domain.dto.request.CreateGroupRequestDto;
import com.project.user_service.domain.dto.response.GroupResponseDto;
import com.project.user_service.domain.entity.groups.Group;
import com.project.user_service.domain.entity.groups.ImageGroup;
import com.project.user_service.domain.entity.users.Users;
import com.project.user_service.domain.enums.RedisMethod;
import com.project.user_service.exception.customException.GroupNotFoundException;
import com.project.user_service.exception.customException.ImageUploadFailedException;
import com.project.user_service.exception.customException.UserNotFoundException;
import com.project.user_service.repository.GroupRepository;
import com.project.user_service.repository.ImageGroupRepository;
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
public class GroupService {

    private final GroupRepository groupRepository;
    private final UsersRepository usersRepository;
    private final RedisService redisService;
    private final ImageGroupRepository imageGroupRepository;
    private final KafkaService kafkaService;

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
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
            throw new IllegalArgumentException("Profile image must be less than 5 MB");
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
        if (user == null) {
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
            if (profileImage != null && !profileImage.isEmpty()) {
                validateProfileImage(profileImage);
                profileImageBytes = profileImage.getBytes();
                profileImageType = profileImage.getContentType();
            }
        } catch (IOException e) {
            log.error("Image Upload of profile For Group Fail : {}", e.getMessage());
            throw new ImageUploadFailedException("Failed to profile images");
        }

        try {
            if (bgImage != null && !bgImage.isEmpty()) {
                validateProfileImage(bgImage);
                bgImageBytes = bgImage.getBytes();
                bgImageType = bgImage.getContentType();
            }
        } catch (IOException e) {
            log.error("Image Upload of Background For Group Fail : {}", e.getMessage());
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

        log.info("Group is created by Id : {} and groupName : {} by leader : {} ({})",
                group.getId().toString(), group.getGroupName(), user.getId().toString(), user.getUsername());

        GroupResponseDto responseDto = GroupResponseDto
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

        kafkaService.saveIntoGroupDatabase(responseDto);

        redisService.set(RedisMethod.GROUP_ + group.getId().toString(), responseDto, TIME_REDIS);
        return responseDto;
    }

    public GroupResponseDto getGroup(UUID id, Boolean giveImage) {
        if (id == null) {
            throw new IllegalArgumentException("Id can't be Null");
        }

        GroupResponseDto groupResponseDto = redisService.get(id.toString(), GroupResponseDto.class);
        if (groupResponseDto != null) {

            redisService.set(RedisMethod.GROUP_ + id.toString(), groupResponseDto, TIME_REDIS);

            if ((groupResponseDto.getProfileImage() != null) || giveImage == false) {
                return groupResponseDto;
            }

            ImageGroup imageGroup = imageGroupRepository.findById(id).orElse(null);
            if (imageGroup == null) {
                return groupResponseDto;
            }
            groupResponseDto.setBgImage(imageGroup.getBgImage());
            groupResponseDto.setBgImageType(imageGroup.getBgImageType());
            groupResponseDto.setProfileImage(imageGroup.getProfileImage());
            groupResponseDto.setProfileImageType(imageGroup.getProfileImageType());

            return groupResponseDto;
        }

        Optional<GroupResponseDto> getResponseOption = groupRepository.getGroupById(id);
        if (getResponseOption.isEmpty()) {
            throw new GroupNotFoundException("Group : " + id);
        }

        long time = TIME_REDIS + getResponseOption.get().getMemberCount();
        time = Math.min(time, TIME_REDIS_MAX);
        redisService.set(RedisMethod.GROUP_ + id.toString(), getResponseOption.get(), time);
        if (giveImage == false) {
            return getResponseOption.get();
        }

        ImageGroup imageGroup = imageGroupRepository.findById(id).orElse(null);
        if (imageGroup == null) {
            return getResponseOption.get();
        }

        groupResponseDto = getResponseOption.get();
        groupResponseDto.setProfileImageType(imageGroup.getProfileImageType());
        groupResponseDto.setProfileImage(imageGroup.getProfileImage());
        groupResponseDto.setBgImage(imageGroup.getBgImage());
        groupResponseDto.setBgImageType(imageGroup.getBgImageType());

        return groupResponseDto;
    }

    @Transactional
    public List<String> updateImage(UUID id, MultipartFile profileImage, MultipartFile bgImage) {
        if (id == null) {
            log.error("Group can't update the image due to null id");
            throw new GroupNotFoundException("Group Id cannot be null");
        }
        List<String> imageUpdate = new ArrayList<>();

        ImageGroup imageGroup = groupRepository.getGroupImageById(id);
        if (imageGroup == null) {
            throw new GroupNotFoundException("Image not found for Group : " + id);
        }

        byte[] profileImageBytes = null;
        byte[] bgImageBytes = null;
        String profileImageType = null;
        String bgImageType = null;

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                validateProfileImage(profileImage);
                profileImageBytes = profileImage.getBytes();
                profileImageType = profileImage.getContentType();

                imageGroup.setProfileImage(profileImageBytes);
                imageGroup.setProfileImageType(profileImageType);
                imageUpdate.add("Profile Image");
            }
        } catch (IOException e) {
            log.error("Image Upload of profile For Group Fail : {}", e.getMessage());
            throw new ImageUploadFailedException("Failed to profile images");
        }

        try {
            if (bgImage != null && !bgImage.isEmpty()) {
                validateProfileImage(bgImage);
                bgImageBytes = bgImage.getBytes();
                bgImageType = bgImage.getContentType();

                imageGroup.setBgImage(bgImageBytes);
                imageGroup.setBgImageType(bgImageType);

                imageUpdate.add("Background Image");
            }
        } catch (IOException e) {
            log.error("Image Upload of Background For Group Fail : {}", e.getMessage());
            throw new ImageUploadFailedException("Failed to background images");
        }

        imageGroupRepository.save(imageGroup);
        redisService.delete(RedisMethod.GROUP_ + id.toString());

        log.info("Group : {} is updated with {}", id.toString(), imageUpdate.toString());
        return imageUpdate;
    }

    @Transactional
    public Boolean updateBio(BioUpdateGroupRequestDto requestDto){

        if(requestDto.getId() == null || requestDto.getBio() == null){
            throw new IllegalArgumentException("Request Missing Id or Bio");
        }

        int update = groupRepository.updateTheBio(requestDto.getId() , requestDto.getBio());

        if(update == 0){
            throw new GroupNotFoundException("Group : " + requestDto.getId().toString());
        }
        redisService.delete(RedisMethod.GROUP_ + requestDto.getId().toString());
        log.info("Group : {} is updated their bio" , requestDto.getId().toString());

        return true;
    }

}
