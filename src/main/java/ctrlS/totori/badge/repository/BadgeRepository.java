package ctrlS.totori.badge.repository;

import ctrlS.totori.badge.entity.Badge;
import ctrlS.totori.badge.entity.BadgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByCategoryAndLevel(BadgeCategory category, int level);

    List<Badge> findAllByCategoryOrderByLevelAsc(BadgeCategory category);
}
