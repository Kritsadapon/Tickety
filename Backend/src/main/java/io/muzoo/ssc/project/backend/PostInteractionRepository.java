package io.muzoo.ssc.project.backend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
    Optional<PostInteraction> findByUserAndPost(User user, Post post);
    int countByPostAndLiked(Post post, boolean liked);
    List<PostInteraction> findByPost(Post post);
}