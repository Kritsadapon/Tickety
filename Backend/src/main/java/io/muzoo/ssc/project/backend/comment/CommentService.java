package io.muzoo.ssc.project.backend.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.muzoo.ssc.project.backend.Comment;
import io.muzoo.ssc.project.backend.CommentDTO;
import io.muzoo.ssc.project.backend.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<CommentDTO> findByPostId(Long postId) {
        return commentRepository.findCommentsByPostId(postId);
    }

    // get comment by user id
    public List<CommentDTO> findByUserId(Long userId) {
        return commentRepository.findCommentsByUserId(userId);
    }
}