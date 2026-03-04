package com.snapia.domain.repository;

import com.snapia.domain.constant.FollowStatus;
import com.snapia.domain.dto.FollowListDTO;
import com.snapia.domain.entity.FollowList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowListRepository extends JpaRepository<FollowList, Long> {

    // 팔로우 관계 조회 (상태 무관)
    Optional<FollowList> findByFromUserUidAndToUserUid(Long fromUserUid, Long toUserUid);

    // 팔로우 관계 존재 여부 확인 (ACCEPT 상태만)
    boolean existsByFromUserUidAndToUserUidAndFollowStatus(Long fromUserUid, Long toUserUid, FollowStatus followStatus);

    // 팔로우 관계 삭제 (⭐ @Modifying 추가 필요)
    @Modifying
    void deleteByFromUserUidAndToUserUid(Long fromUserUid, Long toUserUid);

    // 팔로워 수 조회 (ACCEPT 상태만)
    long countByToUserUidAndFollowStatus(Long userUid, FollowStatus followStatus);

    // 팔로잉 수 조회 (ACCEPT 상태만)
    long countByFromUserUidAndFollowStatus(Long userUid, FollowStatus followStatus);

    // 팔로워 리스트 가져오기 (나를 팔로우하는 사람들 - ACCEPT 상태만)
    @Query("SELECT new com.snapia.domain.dto.FollowListDTO(" +
            "u.uid, u.snapiaId, u.profileImageUrl, " +
            "CASE WHEN f2.fid IS NOT NULL AND f2.followStatus = 'ACCEPT' THEN 1 ELSE 0 END, " +
            "CASE WHEN u.uid = :principalId THEN 1 ELSE 0 END) " +
            "FROM Users u " +
            "INNER JOIN FollowList f ON u.uid = f.fromUser.uid " +
            "LEFT JOIN FollowList f2 ON f2.fromUser.uid = :principalId AND f2.toUser.uid = u.uid " +
            "WHERE f.toUser.uid = :pageOwnerId AND f.followStatus = 'ACCEPT'")
    List<FollowListDTO> findFollowerList(@Param("principalId") Long principalId, @Param("pageOwnerId") Long pageOwnerId);

    // 팔로잉 리스트 가져오기 (내가 팔로우하는 사람들 - ACCEPT 상태만)
    @Query("SELECT new com.snapia.domain.dto.FollowListDTO(" +
            "u.uid, u.snapiaId, u.profileImageUrl, " +
            "CASE WHEN f2.fid IS NOT NULL AND f2.followStatus = 'ACCEPT' THEN 1 ELSE 0 END, " +
            "CASE WHEN u.uid = :principalId THEN 1 ELSE 0 END) " +
            "FROM Users u " +
            "INNER JOIN FollowList f ON u.uid = f.toUser.uid " +
            "LEFT JOIN FollowList f2 ON f2.fromUser.uid = :principalId AND f2.toUser.uid = u.uid " +
            "WHERE f.fromUser.uid = :pageOwnerId AND f.followStatus = 'ACCEPT'")
    List<FollowListDTO> findFollowingList(@Param("principalId") Long principalId, @Param("pageOwnerId") Long pageOwnerId);

    // 팔로우 요청 대기 목록 조회 (AWAITING 상태만)
    @Query("SELECT new com.snapia.domain.dto.FollowListDTO(" +
            "u.uid, u.snapiaId, u.profileImageUrl, " +
            "0, " +  // followState는 요청 상태이므로 0
            "0) " +  // equalUserState는 본인이 아니므로 0
            "FROM Users u " +
            "INNER JOIN FollowList f ON u.uid = f.fromUser.uid " +
            "WHERE f.toUser.uid = :userId AND f.followStatus = 'AWAITING'")
    List<FollowListDTO> findPendingFollowRequests(@Param("userId") Long userId);

    // 내가 팔로우하는 사람들의 uid 목록 (피드용)
    @Query("SELECT f.toUser.uid FROM FollowList f WHERE f.fromUser.uid = :userId AND f.followStatus = 'ACCEPT'")
    List<Long> findFollowingUserIds(@Param("userId") Long userId);
}