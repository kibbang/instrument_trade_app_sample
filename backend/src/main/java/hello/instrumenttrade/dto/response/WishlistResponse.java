package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.Wishlist;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WishlistResponse {
    private final Long instrumentId;
    private final String title;
    private final Integer price;
    private final String region;
    private final String thumbnailUrl;
    private final LocalDateTime wishedAt;

    private WishlistResponse(Wishlist wishlist) {
        this.instrumentId = wishlist.getInstrument().getId();
        this.title = wishlist.getInstrument().getTitle();
        this.price = wishlist.getInstrument().getPrice();
        this.region = wishlist.getInstrument().getRegion();
        this.thumbnailUrl = wishlist.getInstrument().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(null);
        this.wishedAt = wishlist.getCreatedAt();
    }

    public static WishlistResponse from(Wishlist wishlist) {
        return new WishlistResponse(wishlist);
    }
}
