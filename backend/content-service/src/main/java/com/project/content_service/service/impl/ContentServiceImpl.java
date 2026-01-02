package com.project.content_service.service.impl;

import com.project.content_service.domain.entity.Content;
import com.project.content_service.dto.request.CreateContentRequest;
import com.project.content_service.dto.request.EditBioRequest;
import com.project.content_service.dto.request.EditTagsRequest;
import com.project.content_service.dto.request.UpdateContentRequest;
import com.project.content_service.dto.response.ContentResponse;
import com.project.content_service.mapper.ContentMapper;
import com.project.content_service.repository.ContentRepository;
import com.project.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    /**
     * Create a new content
     */
    @Override
    public ContentResponse createContent(CreateContentRequest request) {
        log.info("Creating new content with title: {}", request.getTitle());

        Content content = Content.builder()
                .title(request.getTitle())
                .animeCategories(request.getAnimeCategories())
                .genre(request.getGenre())
                .bio(request.getBio())
                .enable(true)
                .created(LocalDateTime.now())
                .build();

        Content savedContent = contentRepository.save(content);
        log.info("Content created successfully with ID: {}", savedContent.getId());
        return contentMapper.toResponse(savedContent);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContentResponse> getContentById(UUID contentId) {
        log.info("Fetching content with ID: {}", contentId);
        return contentRepository.findById(contentId)
                .map(contentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getAllContent() {
        log.info("Fetching all content");
        return contentRepository.findAll()
                .stream()
                .map(contentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteContent(UUID contentId) {
        log.info("Deleting content with ID: {}", contentId);
        if (!contentRepository.existsById(contentId)) {
            log.warn("Content with ID: {} not found", contentId);
            throw new IllegalArgumentException("Content with ID: " + contentId + " not found");
        }
        contentRepository.deleteById(contentId);
        log.info("Content deleted successfully with ID: {}", contentId);
    }

    @Override
    public ContentResponse disableContent(UUID contentId) {
        log.info("Disabling content with ID: {}", contentId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("Content with ID: {} not found", contentId);
                    return new IllegalArgumentException("Content with ID: " + contentId + " not found");
                });

        content.setEnable(false);
        Content updatedContent = contentRepository.save(content);
        log.info("Content disabled successfully with ID: {}", contentId);
        return contentMapper.toResponse(updatedContent);
    }

    /**
     * Enable content (set enable to true)
     */
    @Override
    public ContentResponse enableContent(UUID contentId) {
        log.info("Enabling content with ID: {}", contentId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("Content with ID: {} not found", contentId);
                    return new IllegalArgumentException("Content with ID: " + contentId + " not found");
                });

        content.setEnable(true);
        Content updatedContent = contentRepository.save(content);
        log.info("Content enabled successfully with ID: {}", contentId);
        return contentMapper.toResponse(updatedContent);
    }

    /**
     * Edit bio of content
     */
    @Override
    public ContentResponse editBio(UUID contentId, EditBioRequest request) {
        log.info("Editing bio for content with ID: {}", contentId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("Content with ID: {} not found", contentId);
                    return new IllegalArgumentException("Content with ID: " + contentId + " not found");
                });

        content.setBio(request.getBio());
        Content updatedContent = contentRepository.save(content);
        log.info("Bio updated successfully for content with ID: {}", contentId);
        return contentMapper.toResponse(updatedContent);
    }

    /**
     * Edit tags (categories and genres) of content
     */
    @Override
    public ContentResponse editTags(UUID contentId, EditTagsRequest request) {
        log.info("Editing tags for content with ID: {}", contentId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("Content with ID: {} not found", contentId);
                    return new IllegalArgumentException("Content with ID: " + contentId + " not found");
                });

        if (request.getAnimeCategories() != null) {
            content.setAnimeCategories(request.getAnimeCategories());
            log.debug("Categories updated for content ID: {}", contentId);
        }

        if (request.getGenre() != null) {
            content.setGenre(request.getGenre());
            log.debug("Genres updated for content ID: {}", contentId);
        }

        Content updatedContent = contentRepository.save(content);
        log.info("Tags updated successfully for content with ID: {}", contentId);
        return contentMapper.toResponse(updatedContent);
    }

    /**
     * Update content
     */
    @Override
    public ContentResponse updateContent(UUID contentId, UpdateContentRequest request) {
        log.info("Updating content with ID: {}", contentId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> {
                    log.error("Content with ID: {} not found", contentId);
                    return new IllegalArgumentException("Content with ID: " + contentId + " not found");
                });

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            content.setTitle(request.getTitle());
        }

        if (request.getAnimeCategories() != null) {
            content.setAnimeCategories(request.getAnimeCategories());
        }

        if (request.getGenre() != null) {
            content.setGenre(request.getGenre());
        }

        if (request.getBio() != null) {
            content.setBio(request.getBio());
        }

        Content updatedContent = contentRepository.save(content);
        log.info("Content updated successfully with ID: {}", contentId);
        return contentMapper.toResponse(updatedContent);
    }
}
