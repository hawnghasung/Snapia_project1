package com.snapia.domain.repository;

import com.snapia.domain.entity.BusinessNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessNotificationRepository extends JpaRepository<BusinessNotification, Long> {
}
