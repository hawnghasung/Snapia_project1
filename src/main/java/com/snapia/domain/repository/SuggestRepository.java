package com.snapia.domain.repository;

import com.snapia.domain.entity.Suggest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestRepository extends JpaRepository<Suggest, Long> {
}
