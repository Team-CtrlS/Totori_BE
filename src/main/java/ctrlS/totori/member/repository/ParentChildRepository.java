package ctrlS.totori.member.repository;

import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.ParentChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParentChildRepository extends JpaRepository<ParentChild, Long> {

    boolean existsByParentAndChild(Member parent, Member child);
    void deleteByParentId(Long parentId);
    void deleteByChildId(Long childId);

    @Query("SELECT pc.child FROM ParentChild pc WHERE pc.parent.id = : parentId")
    List<Member> findChildrenByParentId(Long memberId);
}
