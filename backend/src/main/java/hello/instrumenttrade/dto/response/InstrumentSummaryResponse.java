package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.enums.TradeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class InstrumentSummaryResponse {
    private Long id;
    private String title;
    private Integer price;
    private String region;
    private TradeStatus status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerNickname;

    public InstrumentSummaryResponse(Long id, String title, Integer price, String region,
                                      TradeStatus status, Integer viewCount, LocalDateTime createdAt,
                                      String thumbnailUrl, Long categoryId, String categoryName,
                                      Long sellerId, String sellerNickname) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.region = region;
        this.status = status;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.thumbnailUrl = thumbnailUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.sellerId = sellerId;
        this.sellerNickname = sellerNickname;
    }
}
