package bamboo.repository;

import bamboo.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity,Long> {
}
