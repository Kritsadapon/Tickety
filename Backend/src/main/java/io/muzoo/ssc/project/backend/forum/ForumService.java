package io.muzoo.ssc.project.backend.forum;

import java.rmi.ServerException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.muzoo.ssc.project.backend.Forum;
import io.muzoo.ssc.project.backend.ForumFollow;
import io.muzoo.ssc.project.backend.ForumFollowRepository;
import io.muzoo.ssc.project.backend.ForumRepository;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import io.muzoo.ssc.project.backend.Post;
import io.muzoo.ssc.project.backend.PostRepository;
import io.muzoo.ssc.project.backend.PostInteractionRepository;
import io.muzoo.ssc.project.backend.PostInteraction;

@Service
public class ForumService {
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final ForumFollowRepository forumFollowRepository;
    private final PostRepository postRepository;
    private final PostInteractionRepository postInteractionRepository;

    @Autowired
    public ForumService(ForumRepository forumRepository, UserRepository userRepository, ForumFollowRepository forumFollowRepository, PostRepository postRepository, PostInteractionRepository postInteractionRepository) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.forumFollowRepository = forumFollowRepository;
        this.postRepository = postRepository;
        this.postInteractionRepository = postInteractionRepository;
    }

    // get forum by user id
    public List<Forum> getForumsByUserId(Long userId) {
        return forumRepository.findByUserId(userId);
    }

    // get forum by id
    public Forum getForumById(Long id) {
        return forumRepository.findById(id).orElseThrow(() -> new RuntimeException("Forum not found"));
    }

    // create a new forum post
    public Forum createForum(Forum forum) {
        User currentUser = getAuthenticatedUser();
        forum.setUser(currentUser);
        return forumRepository.save(forum);
    }

    // update forum (for creator and admin)
    public Forum updateForum(Long id, Forum updatedForum) throws ServerException {
        User currentUser = getAuthenticatedUser();
        Forum forum = getForumById(id);

        if (!forum.getUser().equals(currentUser) && !currentUser.getRole().equals("admin")) {
            throw new ServerException("You are not authorized to update this forum.");
        }
        forum.setForumTitle(updatedForum.getForumTitle());
        forum.setForumDescription(updatedForum.getForumDescription());
        return forumRepository.save(forum);
    }

    // delete forum (for creator and admin)
    public void deleteForum(Long id) {
        User currentUser = getAuthenticatedUser();
        Forum forum = getForumById(id);

        if (forum.getUser().getId() != currentUser.getId()) {
            throw new SecurityException("You are not authorized to delete this forum.");
        }

        // First delete all follower records
        List<ForumFollow> followers = forumFollowRepository.findByForum(forum);
        forumFollowRepository.deleteAll(followers);

        // Then delete all posts and their interactions
        List<Post> posts = postRepository.findByForum(forum);
        for (Post post : posts) {
            // Delete all post interactions
            List<PostInteraction> interactions = postInteractionRepository.findByPost(post);
            postInteractionRepository.deleteAll(interactions);
            // Delete the post
            postRepository.delete(post);
        }

        // Finally delete the forum
        forumRepository.delete(forum);
    }

    // get currently authenticated user
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // Follow a forum
    public void followForum(Long forumId) {
        User currentUser = getAuthenticatedUser();
        Forum forum = forumRepository.findById(forumId).orElseThrow(() -> new RuntimeException("Forum not found"));

        Optional<ForumFollow> existingFollow = forumFollowRepository.findByForumAndUser(forum, currentUser);
        if (existingFollow.isPresent()) {
            throw new RuntimeException("You are already following this forum.");
        }

        ForumFollow forumFollow = new ForumFollow(forum, currentUser);
        forumFollowRepository.save(forumFollow);
    }

    // Unfollow a forum
    public void unfollowForum(Long forumId) {
        User currentUser = getAuthenticatedUser();
        Forum forum = forumRepository.findById(forumId).orElseThrow(() -> new RuntimeException("Forum not found"));

        ForumFollow forumFollow = forumFollowRepository.findByForumAndUser(forum, currentUser)
                .orElseThrow(() -> new RuntimeException("You are not following this forum."));

        forumFollowRepository.delete(forumFollow);
    }

    // Get all forums followed by the current user
    public List<ForumFollow> getFollowedForums() {
        User currentUser = getAuthenticatedUser();
        return forumFollowRepository.findByUser(currentUser);
    }

    List<Map<String, Object>> getForumDataList(List<Forum> forums) {
        return forums.stream().map(forum -> {
            Map<String, Object> forumData = new HashMap<>();
            forumData.put("forumid", forum.getForumid());
            forumData.put("forumTitle", forum.getForumTitle());
            forumData.put("forumDescription", forum.getForumDescription());
            forumData.put("forumDateTime", forum.getForumDateTime());
            forumData.put("user", forum.getUser());
            forumData.put("followerCount", forumFollowRepository.countByForum(forum));
            return forumData;
        }).collect(Collectors.toList());
    }

    public List<Forum> searchForums(String keyword) {
        return forumRepository.findByForumTitleContainingIgnoreCaseOrForumDescriptionContainingIgnoreCase(keyword, keyword);
    }

    // for followers
    public List<Map<String, Object>> getAllForumsWithFollowerCount() {
        List<Object[]> results = forumRepository.findAllForumsWithFollowerCount();
        List<Map<String, Object>> forumDataList = new ArrayList<>();

        for (Object[] result : results) {
            Forum forum = (Forum) result[0];
            long followerCount = (long) result[1];

            Map<String, Object> forumData = new HashMap<>();
            forumData.put("forumid", forum.getForumid());
            forumData.put("forumTitle", forum.getForumTitle());
            forumData.put("forumDescription", forum.getForumDescription());
            forumData.put("forumDateTime", forum.getForumDateTime());
            forumData.put("user", forum.getUser());
            forumData.put("followerCount", followerCount);

            forumDataList.add(forumData);
        }
        return forumDataList;
    }
}
