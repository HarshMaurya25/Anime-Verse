package com.project.content_service.service;

import com.project.content_service.dto.request.CreateContentRequest;
import com.project.content_service.dto.request.EditBioRequest;
import com.project.content_service.dto.request.EditTagsRequest;
import com.project.content_service.dto.request.UpdateContentRequest;
import com.project.content_service.dto.response.ContentResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentService {
    ContentResponse createContent(CreateContentRequest request);

    Optional<ContentResponse> getContentById(UUID contentId);

    List<ContentResponse> getAllContent();

    void deleteContent(UUID contentId);

    ContentResponse disableContent(UUID contentId);

    ContentResponse enableContent(UUID contentId);

    ContentResponse editBio(UUID contentId, EditBioRequest request);

    ContentResponse editTags(UUID contentId, EditTagsRequest request);

    ContentResponse updateContent(UUID contentId, UpdateContentRequest request);
}
