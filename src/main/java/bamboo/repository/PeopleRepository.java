package bamboo.repository;

import bamboo.entity.PeopleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeopleRepository extends JpaRepository<PeopleEntity,String> {
}
