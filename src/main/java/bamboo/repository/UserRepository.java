package bamboo.repository;


import bamboo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findByName(String name);

    Optional<UserEntity> findByNickname(String nickname);
}
