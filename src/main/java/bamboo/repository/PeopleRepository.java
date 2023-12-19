package bamboo.repository;

import bamboo.entity.PeopleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<PeopleEntity,String> {
    Optional<PeopleEntity> findByName(String name);
}
