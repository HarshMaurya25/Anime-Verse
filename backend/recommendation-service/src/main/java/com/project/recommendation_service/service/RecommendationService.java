package com.project.recommendation_service.service;

import com.project.recommendation_service.repository.InteractRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RecommendationService {

    private final InteractRepository interactRepository;

//    public Set<> getRecommendation(UUID id){
//
//    }
}
