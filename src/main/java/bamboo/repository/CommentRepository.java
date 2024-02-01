package bamboo.repository;

import bamboo.entity.CommentEntity;
import bamboo.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    List<CommentEntity> findByPostNo(Long postNo);
}

