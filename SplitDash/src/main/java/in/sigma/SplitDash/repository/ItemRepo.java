package in.sigma.SplitDash.repository;

import in.sigma.SplitDash.entity.Item;
import in.sigma.SplitDash.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {
    List<Item> findBySessionId(String sessionId);
    List<Item> findBySessionIdAndStatus(String sessionId, ItemStatus status);
    Optional<Item> findByIdAndSessionId(Long id, String sessionId);

    @Query("SELECT i FROM Item i WHERE i.sessionId = :sessionId AND i.status = 'UNCLAIMED' ORDER BY i.lastUpdated ASC")
    List<Item> findUnclaimedItemsBySessionId(@Param("sessionId") String sessionId);
}
