package com.snapia.domain.repository;

import com.snapia.domain.entity.Room;
import com.snapia.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE (r.sender = :userA AND r.receiver = :userB) OR (r.sender = :userB AND r.receiver = :userA)")
    Optional<Room> findRoomByUsers(@Param("userA") Users userA, @Param("userB") Users userB);

    @Query("SELECT r FROM Room r WHERE r.sender = :user OR r.receiver = :user")
    List<Room> findRoomsByUser(@Param("user") Users user);
}
