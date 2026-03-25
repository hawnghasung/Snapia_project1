package com.snapia.domain.repository;

import com.snapia.domain.entity.Suggest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuggestRepository extends JpaRepository<Suggest, Long> {
    Optional<Suggest> findByAppointmentAid(Long aid);


}
