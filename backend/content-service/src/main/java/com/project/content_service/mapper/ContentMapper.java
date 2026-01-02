package com.project.content_service.mapper;

import com.project.content_service.domain.entity.Content;
import com.project.content_service.dto.request.CreateContentRequest;
import com.project.content_service.dto.response.ContentResponse;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public ContentResponse toResponse(Content content) {
        if (content == null) {
            return null;
        }

        return ContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .animeCategories(content.getAnimeCategories())
                .genre(content.getGenre())
                .bio(content.getBio())
                .enable(content.getEnable())
                .created(content.getCreated())
                .build();
    }

    public Content toEntity(CreateContentRequest request) {
        if (request == null) {
            return null;
        }

        return Content.builder()
                .title(request.getTitle())
                .animeCategories(request.getAnimeCategories())
                .genre(request.getGenre())
                .bio(request.getBio())
                .build();
    }
}
