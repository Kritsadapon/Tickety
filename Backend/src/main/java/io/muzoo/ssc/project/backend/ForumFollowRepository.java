package io.muzoo.ssc.project.backend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumFollowRepository extends JpaRepository<ForumFollow, Long> {
    Optional<ForumFollow> findByForumAndUser(Forum forum, User user);
    List<ForumFollow> findByUser(User user);
    List<ForumFollow> findByForum(Forum forum);

    Long countByForum(Forum forum);
}