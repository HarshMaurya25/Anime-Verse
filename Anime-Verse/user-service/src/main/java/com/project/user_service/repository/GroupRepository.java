package com.project.user_service.repository;

import com.project.user_service.domain.entity.groups.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group , UUID> {
}
