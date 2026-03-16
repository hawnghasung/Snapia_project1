package com.snapia.domain.repository;

import com.snapia.domain.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByRequesterUid(Long requesterUid);
    List<Appointment> findByBusinessProfileBpid(Long bpid);
}
