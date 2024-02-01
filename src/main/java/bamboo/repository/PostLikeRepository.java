package bamboo.repository;

import bamboo.entity.PostLikeEntity;
import bamboo.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity,Long> {
    Optional<PostLikeEntity> findLikeNoByPostNoAndUserNo(Long postNo, int userNo);

    int countByPostNo(Long postNo);

    void deleteByPostNo(Long postNo);
}
