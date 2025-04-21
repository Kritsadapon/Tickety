package io.muzoo.ssc.project.backend.forum;

import java.rmi.ServerException;
import java.util.List;
import java.util.Map;

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
import io.muzoo.ssc.project.backend.ForumFollow;
import io.muzoo.ssc.project.backend.ForumFollowRepository;
import io.muzoo.ssc.project.backend.ForumRepository;
import io.muzoo.ssc.project.backend.SimpleResponseDTO;

@RestController
@RequestMapping("/api/forums")
public class ForumController {
    @Autowired
    private ForumService forumService;

    @Autowired
    private ForumFollowRepository forumFollowRepository;

    @Autowired
    private ForumRepository forumRepository;

    // all forums
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllForums() {
        return ResponseEntity.ok(forumService.getAllForumsWithFollowerCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getForumById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(forumService.getForumById(id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getForumByUserId(@PathVariable Long userId) {
        List<Forum> forums = forumService.getForumsByUserId(userId);
        if (forums.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(forums);
        }
        return ResponseEntity.ok(forums);
    }

    @PostMapping("/create")
    public ResponseEntity<SimpleResponseDTO> createForum(@RequestBody Forum forum) {
        try {
            if (forumRepository.existsByForumTitle(forum.getForumTitle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(SimpleResponseDTO.builder()
                        .success(false).message("A forum with this title already exists.")
                        .build());
            }

            forumService.createForum(forum);
            return ResponseEntity.status(HttpStatus.CREATED).body(SimpleResponseDTO.builder()
                    .success(true).message("Forum created successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SimpleResponseDTO> updateForum(@PathVariable Long id, @RequestBody Forum forum) {
        try {
            forumService.updateForum(id, forum);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true).message("Forum updated successfully")
                    .build());
        }
        catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(SimpleResponseDTO.builder().success(false)
                            .message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SimpleResponseDTO> deleteForum(@PathVariable Long id) {
        try {
            forumService.deleteForum(id);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true).message("Forum deleted successfully")
                    .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(SimpleResponseDTO.builder().success(false)
                            .message(e.getMessage()).build());
        }
    }

    @PostMapping("/{forumId}/follow")
    public ResponseEntity<SimpleResponseDTO> followForum(@PathVariable Long forumId) {
        try {
            forumService.followForum(forumId);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true).message("Forum followed successfully").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/{forumId}/unfollow")
    public ResponseEntity<SimpleResponseDTO> unfollowForum(@PathVariable Long forumId) {
        try {
            forumService.unfollowForum(forumId);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true).message("Forum unfollowed successfully").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponseDTO.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @GetMapping("/followed")
    public ResponseEntity<List<ForumFollow>> getFollowedForums() {
        return ResponseEntity.ok(forumService.getFollowedForums());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchForums(@RequestParam String keyword) {
        List<Forum> forums = forumService.searchForums(keyword);
        return ResponseEntity.ok(forumService.getForumDataList(forums));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Map<String, Object>>> filterForums(@RequestParam String sortBy) {
        List<Forum> forums;
        switch (sortBy.toLowerCase()) {
            case "newest":
                forums = forumRepository.findAllByOrderByForumDateTimeDesc();
                break;
            case "oldest":
                forums = forumRepository.findAllByOrderByForumDateTimeAsc();
                break;
            case "most_followed":
                forums = forumRepository.findAllOrderByFollowsDesc();
                break;
            case "least_followed":
                forums = forumRepository.findAllOrderByFollowsAsc();
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }

        return ResponseEntity.ok(forumService.getForumDataList(forums));
    }
}
