package io.muzoo.ssc.project.backend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // find all posts created by specific user
    List<Post> findByUser(User user);

    // find all posts in specific forum
    List<Post> findByForum(Forum forum);

    Optional<Post> findByPostid(Long postid);

    boolean existsByForumAndPostTitle(Forum forum, String postTitle);

    @Query("SELECT p FROM Post p WHERE p.forum = :forum AND (LOWER(p.postTitle) LIKE %:keyword% OR LOWER(p.postDescription) LIKE %:keyword%)")
    List<Post> searchByForumAndKeyword(@Param("forum") Forum forum, @Param("keyword") String keyword);

    @Query("SELECT p FROM Post p JOIN FETCH p.forum WHERE p.postid = :postId")
    Optional<Post> findPostWithForumById(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.postid = :postId")
    Optional<Post> findPostWithComments(Long postId);

    @Query("SELECT p FROM Post p " +
       "JOIN FETCH p.forum " +
       "JOIN FETCH p.user " +
       "LEFT JOIN FETCH p.comments " +
       "WHERE p.postid = :postId")
    Optional<Post> findPostWithAllRelationships(@Param("postId") Long postId);

    // newest posts first
    List<Post> findByForumOrderByPostDateTimeDesc(Forum forum);

    // oldest posts first
    List<Post> findByForumOrderByPostDateTimeAsc(Forum forum);

    // get posts ordered by most liked
    @Query("SELECT p FROM Post p LEFT JOIN PostInteraction pi ON p.postid = pi.post.postid WHERE p.forum.forumid = :forumId GROUP BY p.postid ORDER BY COUNT(pi.id) DESC")
    List<Post> findByForumOrderByLikesDesc(Long forumId);

    // get posts ordered by least liked
    @Query("SELECT p FROM Post p LEFT JOIN PostInteraction pi ON p.postid = pi.post.postid WHERE p.forum.forumid = :forumId GROUP BY p.postid ORDER BY COUNT(pi.id) ASC")
    List<Post> findByForumOrderByLikesAsc(Long forumId);

    // get all likes
    @Query("SELECT p, COUNT(pi.id) as likeCount FROM Post p LEFT JOIN PostInteraction pi ON p.postid = pi.post.postid " +
            "GROUP BY p.postid")
    List<Object[]> findAllPostsWithLikes();
}
