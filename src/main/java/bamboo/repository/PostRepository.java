package bamboo.repository;

import bamboo.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity,Long> {
    List<PostEntity> findByCategory(int category);

}
