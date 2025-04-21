package io.muzoo.ssc.project.backend.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.muzoo.ssc.project.backend.Comment;
import io.muzoo.ssc.project.backend.CommentDTO;
import io.muzoo.ssc.project.backend.Post;
import io.muzoo.ssc.project.backend.PostRepository;
import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.Users.UserService;
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService UserService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.findAll();
    }

    @GetMapping("/post/{post_id}")
    public List<CommentDTO> getCommentsByPostId(@PathVariable Long post_id) {
        return commentService.findByPostId(post_id);
    }

    // get comment by user id
    @GetMapping("/user/{userId}")
    public List<CommentDTO> getCommentsByUserId(@PathVariable Long userId) {
        return commentService.findByUserId(userId);
    }

    @PostMapping
    public SimpleResponseDTO createComment(@RequestBody CommentCreateDTO commentDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
    
            User user = UserService.getUserByUsername(username);
            if (user == null) {
                return SimpleResponseDTO.builder()
                        .success(false)
                        .message("User not found")
                        .build();
            }
    
            // Fetch the post using the postId from the DTO
            Post post = postRepository.findById(commentDTO.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));
    
            // Create a new Comment entity
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setPost(post);
            comment.setComment(commentDTO.getComment());
    
            commentService.save(comment);
    
            return SimpleResponseDTO.builder()
                    .success(true)
                    .message("Comment created successfully")
                    .build();
        } catch (Exception e) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("Failed to create comment: " + e.getMessage())
                    .build();
        }
    }

}