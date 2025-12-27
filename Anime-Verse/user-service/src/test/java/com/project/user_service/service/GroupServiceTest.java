package com.project.user_service.service;

import com.project.user_service.domain.dto.request.CreateGroupRequestDto;
import com.project.user_service.domain.dto.response.GroupResponseDto;
import com.project.user_service.domain.entity.groups.Group;
import com.project.user_service.domain.entity.users.Users;
import com.project.user_service.domain.enums.RedisMethod;
import com.project.user_service.exception.customException.ImageUploadFailedException;
import com.project.user_service.exception.customException.UserNotFoundException;
import com.project.user_service.repository.GroupRepository;
import com.project.user_service.repository.UsersRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private GroupService groupService;

    private Users user;

    private Group group1;
    private CreateGroupRequestDto createGroupRequestDto;

    @BeforeEach
    void setUp() {
        user = Users
                .builder()
                .id(UUID.randomUUID())
                .username("test_user_1")
                .bio("test user bio")
                .displayName("Test User")
                .location("Test Location")
                .dateOfBirth(LocalDate.of(2025, 5, 25))
                .build();

        group1 = Group
                .builder()
                .id(UUID.randomUUID())
                .groupName("Test_group")
                .bio("test bio")
                .leader(user)
                .build();

        createGroupRequestDto = CreateGroupRequestDto
                .builder()
                .groupName(group1.getGroupName())
                .leaderId(user.getId())
                .groupBio(group1.getBio())
                .build();

    }

    @Nested
    @DisplayName("When creating a Group")
    class CreateGroupTest {

        @Test
        @DisplayName("Should Create A group without Any problem with null image")
        void testCreateAGroupWithNullImage() {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(user);
            when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
                Group group = invocation.getArgument(0);
                if (group.getId() == null) {
                    group.setId(UUID.randomUUID());
                }
                return group;
            });

            GroupResponseDto requestDto = groupService.createGroup(createGroupRequestDto, null, null);

            assertNotNull(requestDto);
            assertEquals(createGroupRequestDto.getGroupName(), requestDto.getGroupName());
            assertEquals(createGroupRequestDto.getGroupBio(), requestDto.getGroupBio());
            assertNotNull(requestDto.getId());

            verify(redisService).set(eq(RedisMethod.GROUP_ + requestDto.getId().toString()), any(GroupResponseDto.class), anyLong());
        }

        @Test
        @DisplayName("Should throw exception as User not Found")
        void testCreateAGroupWithUserNotFound() {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(null);

            assertThatThrownBy(
                    () -> groupService.createGroup(createGroupRequestDto, null, null))
                    .isInstanceOf(UserNotFoundException.class);

            verify(groupRepository, never()).save(any(Group.class));
            verify(redisService , never()).set(any() , any() , anyLong());
        }

        @Test
        @DisplayName("Should create a group with Image")
        void testCreateGroupWithImage() {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(user);
            when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
                Group group = invocation.getArgument(0);
                if (group.getId() == null) {
                    group.setId(UUID.randomUUID());
                }
                return group;
            });

            byte[] largeImage = new byte[5 * 1024 * 1024];
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.jpg",
                    "image/jpeg",
                    largeImage);

            GroupResponseDto requestDto = groupService.createGroup(createGroupRequestDto, file, file);

            assertNotNull(requestDto);
            assertEquals(createGroupRequestDto.getGroupName(), requestDto.getGroupName());
            assertEquals(createGroupRequestDto.getGroupBio(), requestDto.getGroupBio());
            assertNotNull(requestDto.getId());

            verify(groupRepository).save(any(Group.class));
            verify(redisService).set(eq(RedisMethod.GROUP_ + requestDto.getId().toString()), any(GroupResponseDto.class), anyLong());
        }

        @Test
        @DisplayName("Should throw Exception as file is not image")
        void testCreateGroupWithWrongFile() {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(user);

            byte[] largeImage = new byte[5 * 1024 * 1024];
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.gif",
                    "image/gif",
                    largeImage);

            assertThatThrownBy(
                    () -> groupService.createGroup(createGroupRequestDto, file, null))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(groupRepository, never()).save(any(Group.class));
            verify(redisService , never()).set(any() , any() , anyLong());
        }

        @Test
        @DisplayName("Should throw Exception as file is more than 5 Mb")
        void testCreateGroupWithLargeFile() {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(user);

            byte[] largeImage = new byte[10 * 1024 * 1024];
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "profile.png",
                    "image/png",
                    largeImage);

            assertThatThrownBy(
                    () -> groupService.createGroup(createGroupRequestDto, file, null))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(groupRepository, never()).save(any(Group.class));
            verify(redisService , never()).set(any() , any() , anyLong());
        }

        @Test
        @DisplayName("Should throw ImageUploadFailedException when IOException occurs for Profile Image")
        void testCreateGroupWithIOExceptionWithProfileImage() throws Exception {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(user);

            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getBytes()).thenThrow(new java.io.IOException("File read error"));

            assertThatThrownBy(
                    () -> groupService.createGroup(createGroupRequestDto, mockFile, null))
                    .isInstanceOf(ImageUploadFailedException.class)
                    .hasMessageContaining("Failed to profile images");

            verify(groupRepository, never()).save(any(Group.class));
            verify(redisService , never()).set(any() , any() , anyLong());
        }

        @Test
        @DisplayName("Should throw ImageUploadFailedException when IOException occurs for Bg Image")
        void testCreateGroupWithIOExceptionWithBgImage() throws Exception {
            when(usersRepository.findByIdAndEnableTrue(any())).thenReturn(user);

            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getBytes()).thenThrow(new java.io.IOException("File read error"));

            assertThatThrownBy(
                    () -> groupService.createGroup(createGroupRequestDto, null, mockFile))
                    .isInstanceOf(ImageUploadFailedException.class)
                    .hasMessageContaining("Failed to background images");

            verify(groupRepository, never()).save(any(Group.class));
            verify(redisService , never()).set(any() , any() , anyLong());
        }

    }
}