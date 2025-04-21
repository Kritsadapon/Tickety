package io.muzoo.ssc.project.backend.posts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.muzoo.ssc.project.backend.Forum;
import io.muzoo.ssc.project.backend.ForumRepository;
import io.muzoo.ssc.project.backend.Post;
import io.muzoo.ssc.project.backend.PostInteraction;
import io.muzoo.ssc.project.backend.PostInteractionRepository;
import io.muzoo.ssc.project.backend.PostRepository;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final PostInteractionRepository postInteractionRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ForumRepository forumRepository, PostInteractionRepository postInteractionRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.postInteractionRepository = postInteractionRepository;

    }

    // get post by id
    public Post getPostById(Long id) {
        return postRepository.findPostWithAllRelationships(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // get all posts in forum
    public List<Post> getPostsByForum(Long forumId) {
        Forum forum = forumRepository.findById(forumId).orElseThrow(() -> new RuntimeException("Forum not found"));
        return postRepository.findByForum(forum);
    }

    // get all posts created by a specific user
    public List<Post> getPostsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findByUser(user);
    }

    // create new post
    public Post createPost(Long forumId, Post post) {
        User currentUser = getAuthenticatedUser();
        Forum forum = forumRepository.findById(forumId).orElseThrow(() -> new RuntimeException("Forum not found"));

        post.setUser(currentUser);
        post.setForum(forum);
        return postRepository.save(post);
    }

    public Map<String, Object> getPostWithComments(Long postId) {
        Post post = postRepository.findPostWithComments(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    
        Map<String, Object> postData = new HashMap<>();
        postData.put("postid", post.getPostid());
        postData.put("postTitle", post.getPostTitle());
        postData.put("postDescription", post.getPostDescription());
        postData.put("postDateTime", post.getPostDateTime());
        postData.put("user", post.getUser());
        postData.put("comments", post.getComments());
    
        return postData;
    }

    // update a post (only for creator and admin)
    public Post updatePost(Long id, Post updatedPost) {
        User currentUser = getAuthenticatedUser();
        Post post = getPostById(id);

        if (!post.getUser().equals(currentUser) && !currentUser.getRole().equals("admin")) {
            throw new SecurityException("You are not authorized to update this post.");
        }

        post.setPostTitle(updatedPost.getPostTitle());
        post.setPostDescription(updatedPost.getPostDescription());
        // post.setPostComment(updatedPost.getPostComment());
        return postRepository.save(post);
    }

    // delete a post (only creator and admin)
    public void deletePost(Long id) {
        User currentUser = getAuthenticatedUser();
        Post post = getPostById(id);

        if (!post.getUser().equals(currentUser) && !currentUser.getRole().equals("admin")) {
            throw new SecurityException("You are not authorized to delete this post.");
        }

        // First delete all post interactions
        List<PostInteraction> interactions = postInteractionRepository.findByPost(post);
        postInteractionRepository.deleteAll(interactions);

        // Then delete the post
        postRepository.delete(post);
    }

    // get currently authenticated user
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    public void likePost(Long postId) {
        User currentUser = getAuthenticatedUser();
        Post post = getPostById(postId);

        Optional<PostInteraction> interaction = postInteractionRepository.findByUserAndPost(currentUser, post);

        if (interaction.isPresent()) {
            PostInteraction existingInteraction = interaction.get();
            if (existingInteraction.isLiked()) {
                postInteractionRepository.delete(existingInteraction);
            } else {
                existingInteraction.setLiked(true);
                postInteractionRepository.save(existingInteraction);
            }
        } else {
            PostInteraction newInteraction = new PostInteraction();
            newInteraction.setUser(currentUser);
            newInteraction.setPost(post);
            newInteraction.setLiked(true);
            postInteractionRepository.save(newInteraction);
        }
    }

    public void dislikePost(Long postId) {
        User currentUser = getAuthenticatedUser();
        Post post = getPostById(postId);

        Optional<PostInteraction> interaction = postInteractionRepository.findByUserAndPost(currentUser, post);

        if (interaction.isPresent()) {
            PostInteraction existingInteraction = interaction.get();
            if (!existingInteraction.isLiked()) {
                postInteractionRepository.delete(existingInteraction);
            } else {
                existingInteraction.setLiked(false);
                postInteractionRepository.save(existingInteraction);
            }
        } else {
            PostInteraction newInteraction = new PostInteraction();
            newInteraction.setUser(currentUser);
            newInteraction.setPost(post);
            newInteraction.setLiked(false);
            postInteractionRepository.save(newInteraction);
        }
    }

    public int getLikeCount(Long postId) {
        Post post = getPostById(postId);
        return postInteractionRepository.countByPostAndLiked(post, true);
    }

    public int getDislikeCount(Long postId) {
        Post post = getPostById(postId);
        return postInteractionRepository.countByPostAndLiked(post, false);
    }

    public Boolean getUserInteraction(Long postId) {
        User currentUser = getAuthenticatedUser();
        Post post = getPostById(postId);
    
        Optional<PostInteraction> interaction = postInteractionRepository.findByUserAndPost(currentUser, post);
        return interaction.map(PostInteraction::isLiked).orElse(null);
    }

    List<Map<String, Object>> getPostDataList(List<Post> posts) {
        return posts.stream().map(post -> {
            Map<String, Object> postData = new HashMap<>();
            postData.put("postid", post.getPostid());
            postData.put("postTitle", post.getPostTitle());
            postData.put("postDescription", post.getPostDescription());
            postData.put("postDateTime", post.getPostDateTime());
            postData.put("user", post.getUser());
            postData.put("likeCount", postInteractionRepository.countByPostAndLiked(post, true));
            postData.put("dislikeCount", postInteractionRepository.countByPostAndLiked(post, false));
            return postData;
        }).collect(Collectors.toList());
    }

    // for search posts
    public List<Post> searchPosts(Long forumId, String keyword) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum not found"));
        return postRepository.searchByForumAndKeyword(forum, keyword);
    }

    // for likes
    public List<Map<String, Object>> getAllPostsWithLikeCount() {
        List<Object[]> results = postRepository.findAllPostsWithLikes();
        List<Map<String, Object>> postDataList = new ArrayList<>();

        for (Object[] result : results) {
            Post post = (Post) result[0];
            long likeCount = (long) result[1];

            Map<String, Object> postData = new HashMap<>();
            postData.put("postid", post.getPostid());
            postData.put("postTitle", post.getPostTitle());
            postData.put("postDescription", post.getPostDescription());
            postData.put("postDateTime", post.getPostDateTime());
            postData.put("user", post.getUser());
            postData.put("likeCount", likeCount);

            postDataList.add(postData);
        }
        return postDataList;
    }
}
