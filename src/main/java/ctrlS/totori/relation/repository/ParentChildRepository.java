package ctrlS.totori.relation.repository;

import ctrlS.totori.relation.entity.ParentChild;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentChildRepository extends JpaRepository<ParentChild, Long> {
}
