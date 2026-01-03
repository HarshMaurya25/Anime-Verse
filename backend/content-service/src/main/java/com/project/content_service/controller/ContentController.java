package com.project.content_service.controller;

import com.project.content_service.domain.dto.request.AddCommentRequest;
import com.project.content_service.domain.dto.request.CreateContentRequest;
import com.project.content_service.domain.dto.request.LikeDislikeRequest;
import com.project.content_service.domain.dto.response.CommentResponse;
import com.project.content_service.domain.dto.response.ContentDetailResponse;
import com.project.content_service.domain.dto.response.ContentResponse;
import com.project.content_service.service.ContentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentServiceImpl contentService;

    @PreAuthorize("authentication.principal.id.equals(#request.userID)")
    @PostMapping("/create")
    public ResponseEntity<ContentResponse> createContent(
            @Valid @RequestPart CreateContentRequest request,
            @RequestPart MultipartFile media) {
        ContentResponse response = contentService.createContent(request, media);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<ContentDetailResponse> getContentById(
            @RequestParam UUID contentId,
            @RequestParam(required = false) UUID currentUserId,
            @RequestParam(defaultValue = "true") boolean includeMedia) {
        ContentDetailResponse response = contentService.getContentDetailById(contentId, currentUserId, includeMedia);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/batch")
    public ResponseEntity<Page<ContentDetailResponse>> getContentsByIds(
            @RequestParam List<UUID> contentIds,
            @RequestParam(required = false) UUID currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "true") boolean includeMedia) {
        Page<ContentDetailResponse> responses = contentService.getContentsByIds(contentIds, currentUserId, page,
                includeMedia);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ContentDetailResponse>> getContentsByUserId(
            @RequestParam UUID userId,
            @RequestParam(required = false) UUID currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "true") boolean includeMedia) {
        Page<ContentDetailResponse> responses = contentService.getContentsByUserId(userId, currentUserId, page,
                includeMedia);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('MODERATOR') OR @contentServiceImpl.isOwner(authentication.principal.id, #contentId)")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteContent(@RequestParam UUID contentId) {
        contentService.deleteContent(contentId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MODERATOR') OR @contentServiceImpl.isOwner(authentication.principal.id, #contentId)")
    @PutMapping("/disable")
    public ResponseEntity<Void> disableContent(@RequestParam UUID contentId) {
        contentService.disableContent(contentId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MODERATOR') OR @contentServiceImpl.isOwner(authentication.principal.id, #contentId)")
    @PutMapping("/enable")
    public ResponseEntity<Void> enableContent(@RequestParam UUID contentId) {
        contentService.enableContent(contentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentsByContentId(
            @RequestParam UUID contentId,
            @RequestParam(defaultValue = "0") int page) {
        Page<CommentResponse> comments = contentService.getCommentsByContentId(contentId, page);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("authentication.principal.id.equals(#request.userId)")
    @PostMapping("/comment/add")
    public ResponseEntity<Void> addComment(@Valid @RequestBody AddCommentRequest request) {
        contentService.addComment(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("authentication.principal.id.equals(#request.userId)")
    @PostMapping("/like")
    public ResponseEntity<Void> likeContent(@Valid @RequestBody LikeDislikeRequest request) {
        contentService.likeContent(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("authentication.principal.id.equals(#request.userId)")
    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeContent(@Valid @RequestBody LikeDislikeRequest request) {
        contentService.dislikeContent(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("authentication.principal.id.equals(#request.userId)")
    @DeleteMapping("/like-dislike/remove")
    public ResponseEntity<Void> removeLikeDislike(@Valid @RequestBody LikeDislikeRequest request) {
        contentService.removeLikeDislike(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("authentication.principal.id.equals(#userId)")
    @PostMapping("/share")
    public ResponseEntity<Void> shareContent(
            @RequestParam UUID contentId,
            @RequestParam UUID userId) {
        contentService.shareContent(contentId, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
