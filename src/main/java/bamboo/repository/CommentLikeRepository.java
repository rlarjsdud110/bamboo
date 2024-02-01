package bamboo.repository;

import bamboo.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity,Long> {
    Optional<CommentLikeEntity> findLikeNoByCommentNoAndUserNo(Long commentNo, int userNo);
    void deleteByCommentNo(Long commentNo);

}
