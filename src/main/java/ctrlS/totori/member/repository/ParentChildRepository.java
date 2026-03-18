package ctrlS.totori.member.repository;

import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.ParentChild;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentChildRepository extends JpaRepository<ParentChild, Long> {

    boolean existsByParentAndChild(Member parent, Member child);
}
