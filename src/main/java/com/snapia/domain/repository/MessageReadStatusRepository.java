package com.snapia.domain.repository;

import com.snapia.domain.constant.ReadStatus;
import com.snapia.domain.entity.MessageReadStatus;
import com.snapia.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
    @Query("SELECT mrs FROM MessageReadStatus mrs WHERE mrs.message.room.roid = :roomId AND mrs.users = :user AND mrs.readStatus = :status")
    List<MessageReadStatus> findByRoomAndUserAndStatus(@Param("roomId") Long roomId, @Param("user") Users user, @Param("status") ReadStatus status);

    @Query("SELECT COUNT(mrs) FROM MessageReadStatus mrs WHERE mrs.message.room.roid = :roomId AND mrs.users = :user AND mrs.readStatus = :status")
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("user") Users user, @Param("status") ReadStatus status);
}
