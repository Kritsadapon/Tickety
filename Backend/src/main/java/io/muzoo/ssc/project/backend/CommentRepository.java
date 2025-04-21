package io.muzoo.ssc.project.backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT new io.muzoo.ssc.project.backend.CommentDTO(c.commentId, c.comment, c.user.username, c.dateTime, " +
       "CASE WHEN c.user.profilePicture IS NOT NULL THEN CONCAT('/api/users/', c.user.id, '/profile-picture') ELSE NULL END) " +
       "FROM Comment c WHERE c.post.postid = :postId ORDER BY c.dateTime DESC")
    List<CommentDTO> findCommentsByPostId(Long postId);

    @Query("SELECT new io.muzoo.ssc.project.backend.CommentDTO(c.commentId, c.comment, c.user.username, c.dateTime, " +
           "CASE WHEN c.user.profilePicture IS NOT NULL THEN CONCAT('/api/users/', c.user.id, '/profile-picture') ELSE NULL END) " +
           "FROM Comment c WHERE c.user.id = :userId")
    List<CommentDTO> findCommentsByUserId(Long userId);
}