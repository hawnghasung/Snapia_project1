package com.snapia.domain.controller;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.dto.CommentDTO;
import com.snapia.domain.dto.CommentResponseDTO;
import com.snapia.domain.service.CommentLikeService;
import com.snapia.domain.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    // 댓글 작성
    @PostMapping("/register")
    // - 성공 시 201 반환
    public ResponseEntity<Void> register(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        commentService.register(commentDTO,principalDetails.getUsers().getSnapiaId());

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    // 댓글 수정
     @PutMapping("/{cid}")
    // - 본인 아니면 서비스단에서 예외 던짐
    // - 성공 시 200 반환
    public ResponseEntity<Void> update(@PathVariable Long cid, @RequestBody String content,@AuthenticationPrincipal PrincipalDetails principalDetails) {
        commentService.update(cid,content,principalDetails.getUsers().getSnapiaId());

        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
     @DeleteMapping("/{cid}")
    // - 본인 아니면 서비스단에서 예외 던짐
    // - 성공 시 204 반환
    public ResponseEntity<Void> delete(@PathVariable Long cid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        commentService.delete(cid, principalDetails.getUsers().getSnapiaId());

        return ResponseEntity.noContent().build();
    }

    // 게시물의 댓글 목록 조회
     @GetMapping("/post/{pid}")
    // - Comment 엔티티 직접 반환 금지 → CommentDTO로 변환 필요 (순환참조 위험)
    // - 대댓글 포함 여부 결정 필요
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPost(@PathVariable Long pid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(commentService.getCommentsByPost(pid, principalDetails.getUsers().getSnapiaId()));
    }

    // 대댓글 목록 조회 (선택 - 위에서 같이 내리면 필요 없음)
     @GetMapping("/{cid}/replies")
    public ResponseEntity<List<CommentResponseDTO>> getReplies(@PathVariable Long cid,  @AuthenticationPrincipal PrincipalDetails principalDetails) {
         return ResponseEntity.ok(commentService.getRepliesByComment(cid,principalDetails.getUsers().getSnapiaId()));
    }

    // 댓글 좋아요 토글
     @PostMapping("/{cid}/like")
    // - 좋아요 수 같이 반환하면 프론트에서 편함
    public ResponseEntity<Long> toggleLike(@PathVariable Long cid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        commentLikeService.toggleLike(cid, principalDetails.getUsers().getSnapiaId());
        return ResponseEntity.ok(commentLikeService.getLikeCount(cid));
    }

    // 댓글 좋아요 수 조회 (선택 - 목록 조회 시 같이 내리면 필요 없음)
     @GetMapping("/{cid}/like/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long cid) {
         return ResponseEntity.ok(commentLikeService.getLikeCount(cid));
    }
}