package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.Instrument;
import hello.instrumenttrade.enums.TradeStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class InstrumentDetailResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer price;
    private final String region;
    private final TradeStatus status;
    private final Integer viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long categoryId;
    private final String categoryName;
    private final Long sellerId;
    private final String sellerNickname;
    private final String sellerRegion;
    private final List<InstrumentImageResponse> images;

    private InstrumentDetailResponse(Instrument instrument) {
        this.id = instrument.getId();
        this.title = instrument.getTitle();
        this.description = instrument.getDescription();
        this.price = instrument.getPrice();
        this.region = instrument.getRegion();
        this.status = instrument.getStatus();
        this.viewCount = instrument.getViewCount();
        this.createdAt = instrument.getCreatedAt();
        this.updatedAt = instrument.getUpdatedAt();
        this.categoryId = instrument.getCategory().getId();
        this.categoryName = instrument.getCategory().getName().name();
        this.sellerId = instrument.getUser().getId();
        this.sellerNickname = instrument.getUser().getNickname();
        this.sellerRegion = instrument.getUser().getRegion();
        this.images = instrument.getImages().stream()
                .map(InstrumentImageResponse::from)
                .toList();
    }

    public static InstrumentDetailResponse from(Instrument instrument) {
        return new InstrumentDetailResponse(instrument);
    }
}
