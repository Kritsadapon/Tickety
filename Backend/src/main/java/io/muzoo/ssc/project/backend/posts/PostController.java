package io.muzoo.ssc.project.backend.posts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.muzoo.ssc.project.backend.Forum;
import io.muzoo.ssc.project.backend.ForumRepository;
import io.muzoo.ssc.project.backend.Post;
import io.muzoo.ssc.project.backend.PostInteractionRepository;
import io.muzoo.ssc.project.backend.PostRepository;
import io.muzoo.ssc.project.backend.SimpleResponseDTO;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostInteractionRepository postInteractionRepository;

    @Autowired
    private ForumRepository forumRepository;

    // get all posts
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPostsWithLikeCount());
    }

    @GetMapping("/user/{userId}") //userid
    public ResponseEntity<?> getPostByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(postService.getPostsByUser(userId));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @GetMapping("/forum/{forumId}") //get by forum id
    public ResponseEntity<?> getPostsByForum(@PathVariable Long forumId) {
        try {
            List<Post> posts = postService.getPostsByForum(forumId);
            List<Map<String, Object>> postsWithUserInfo = posts.stream()
                .map(post -> {
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("postid", post.getPostid());
                    postMap.put("postTitle", post.getPostTitle());
                    postMap.put("postDescription", post.getPostDescription());
                    postMap.put("postDateTime", post.getPostDateTime());
                    
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", post.getUser().getId());
                    userMap.put("username", post.getUser().getUsername());
                    userMap.put("profilePictureUrl", post.getUser().getProfilePicture() != null ? 
                        "/api/users/" + post.getUser().getId() + "/profile-picture" : null);
                    
                    postMap.put("user", userMap);
                    return postMap;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(postsWithUserInfo);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseEntity.ok(post); // Return the single post object
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/create/{forumId}") // create post under forum and user id
    public ResponseEntity<SimpleResponseDTO> createPost(@PathVariable Long forumId, @RequestBody Post post) {
        try {
            Forum forum = forumRepository.findById(forumId)
            .orElseThrow(() -> new RuntimeException("Forum not found"));

            if (postRepository.existsByForumAndPostTitle(forum, post.getPostTitle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(SimpleResponseDTO.builder()
                        .success(false).message("A post with this title already exists in this forum.")
                        .build());
            }
            
            postService.createPost(forumId, post);
            return ResponseEntity.status(HttpStatus.CREATED).body(SimpleResponseDTO.builder()
                    .success(true).message("Post created successfully").build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @PutMapping("/update/{id}") // update post by postid
    public ResponseEntity<SimpleResponseDTO> updatePost(@PathVariable Long id, @RequestBody Post post) {
        try {
            postService.updatePost(id, post);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true).message("Post updated successfully").build());
        }
        catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/delete/{id}") // delete by post id
    public ResponseEntity<SimpleResponseDTO> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true).message("Post deleted successfully").build());
        }
        catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<SimpleResponseDTO> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .success(true).message("Post liked successfully").build());
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<SimpleResponseDTO> dislikePost(@PathVariable Long postId) {
        postService.dislikePost(postId);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .success(true).message("Post disliked successfully").build());
    }

    @GetMapping("/{postId}/like-count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getLikeCount(postId));
    }

    @GetMapping("/{postId}/dislike-count")
    public ResponseEntity<Integer> getDislikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getDislikeCount(postId));
    }   

    @GetMapping("/{postId}/interaction")
    public ResponseEntity<?> getUserInteraction(@PathVariable Long postId) {
        try {
            Boolean userInteraction = postService.getUserInteraction(postId);
            return ResponseEntity.ok(userInteraction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    // for search for posts
    @GetMapping("/forum/{forumId}/search")
    public ResponseEntity<List<Map<String, Object>>> searchPosts(@PathVariable Long forumId, @RequestParam String keyword) {
        List<Post> posts = postService.searchPosts(forumId, keyword);
        return ResponseEntity.ok(postService.getPostDataList(posts));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Map<String, Object>>> filterPosts(@RequestParam String sortBy, @RequestParam Long forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum not found"));
        List<Post> posts;

        switch (sortBy.toLowerCase()) {
            case "newest":
                posts = postRepository.findByForumOrderByPostDateTimeDesc(forum);
                break;
            case "oldest":
                posts = postRepository.findByForumOrderByPostDateTimeAsc(forum);
                break;
            case "most_liked":
                posts = postRepository.findByForumOrderByLikesDesc(forumId);
                break;
            case "least_liked":
                posts = postRepository.findByForumOrderByLikesAsc(forumId);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }

        return ResponseEntity.ok(postService.getPostDataList(posts));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getPostWithComments(@PathVariable Long postId) {
        Map<String, Object> postData = postService.getPostWithComments(postId);
        return ResponseEntity.ok(postData);
    }
}
