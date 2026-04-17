package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.TradeRequest;
import hello.instrumenttrade.enums.TradeRequestStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TradeRequestResponse {
    private final Long id;
    private final Long instrumentId;
    private final String instrumentTitle;
    private final Integer instrumentPrice;
    private final String instrumentThumbnail;
    private final Long buyerId;
    private final String buyerNickname;
    private final Long sellerId;
    private final String sellerNickname;
    private final TradeRequestStatus status;
    private final String message;
    private final LocalDateTime createdAt;

    private TradeRequestResponse(TradeRequest tr) {
        this.id = tr.getId();
        this.instrumentId = tr.getInstrument().getId();
        this.instrumentTitle = tr.getInstrument().getTitle();
        this.instrumentPrice = tr.getInstrument().getPrice();
        this.instrumentThumbnail = tr.getInstrument().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .findFirst().map(img -> img.getImageUrl()).orElse(null);
        this.buyerId = tr.getBuyer().getId();
        this.buyerNickname = tr.getBuyer().getNickname();
        this.sellerId = tr.getSeller().getId();
        this.sellerNickname = tr.getSeller().getNickname();
        this.status = tr.getStatus();
        this.message = tr.getMessage();
        this.createdAt = tr.getCreatedAt();
    }

    public static TradeRequestResponse from(TradeRequest tr) {
        return new TradeRequestResponse(tr);
    }
}
