package io.muzoo.ssc.project.backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    // find all forums created by specific user
    List<Forum> findByUser(User user);

    List<Forum> findByForumTitleContainingIgnoreCaseOrForumDescriptionContainingIgnoreCase(String title, String description);

    List<Forum> findByUserId(Long userId);

    boolean existsByForumTitle(String forumTitle);

    // newest forum first
    List<Forum> findAllByOrderByForumDateTimeDesc();

    // oldest forum first
    List<Forum> findAllByOrderByForumDateTimeAsc();

    // most followed
    @Query("SELECT f FROM Forum f LEFT JOIN ForumFollow ff ON f.forumid = ff.forum.forumid GROUP BY f.forumid ORDER BY COUNT(ff.id) DESC")
    List<Forum> findAllOrderByFollowsDesc();

    // least followed
    @Query("SELECT f FROM Forum f LEFT JOIN ForumFollow ff ON f.forumid = ff.forum.forumid GROUP BY f.forumid ORDER BY COUNT(ff.id) ASC")
    List<Forum> findAllOrderByFollowsAsc();

    // get all followers
    @Query("SELECT f, COUNT(ff.id) as followerCount FROM Forum f LEFT JOIN ForumFollow ff ON f.forumid = ff.forum.forumid " +
            "GROUP BY f.forumid")
    List<Object[]> findAllForumsWithFollowerCount();
}
