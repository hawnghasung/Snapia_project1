package com.snapia.domain.repository;

import com.snapia.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.room.roid = :roomId ORDER BY m.createdAt ASC")
    List<Message> findMessagesByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT m FROM Message m WHERE m.room.roid = :roomId ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Message> findLastMessageByRoomId(@Param("roomId") Long roomId);
}
