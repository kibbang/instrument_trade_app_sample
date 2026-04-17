package hello.instrumenttrade.repository;

import hello.instrumenttrade.domain.TradeRequest;
import hello.instrumenttrade.enums.TradeRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TradeRequestRepository extends JpaRepository<TradeRequest, Long> {

    // 구매자로서 내가 보낸 요청
    @Query("SELECT t FROM TradeRequest t JOIN FETCH t.instrument JOIN FETCH t.seller WHERE t.buyer.id = :userId ORDER BY t.createdAt DESC")
    Page<TradeRequest> findByBuyerId(@Param("userId") Long userId, Pageable pageable);

    // 판매자로서 받은 요청
    @Query("SELECT t FROM TradeRequest t JOIN FETCH t.instrument JOIN FETCH t.buyer WHERE t.seller.id = :userId ORDER BY t.createdAt DESC")
    Page<TradeRequest> findBySellerId(@Param("userId") Long userId, Pageable pageable);

    // 특정 악기에 대한 활성 요청 수 (REQUESTED or ACCEPTED)
    long countByInstrumentIdAndStatusIn(Long instrumentId, List<TradeRequestStatus> statuses);

    // 이미 보낸 요청 여부
    boolean existsByInstrumentIdAndBuyerIdAndStatusIn(Long instrumentId, Long buyerId, List<TradeRequestStatus> statuses);
}
