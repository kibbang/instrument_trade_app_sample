package hello.instrumenttrade.repository;

import hello.instrumenttrade.domain.Wishlist;
import hello.instrumenttrade.domain.WishlistId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    boolean existsById(WishlistId id);

    @Query("SELECT w FROM Wishlist w JOIN FETCH w.instrument i WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    Page<Wishlist> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
