package hello.instrumenttrade.domain;

import hello.instrumenttrade.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "wishlists")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist extends BaseTimeEntity {

    @EmbeddedId
    private WishlistId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("instrumentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    public static Wishlist of(User user, Instrument instrument) {
        Wishlist wishlist = new Wishlist();
        wishlist.id = new WishlistId(user.getId(), instrument.getId());
        wishlist.user = user;
        wishlist.instrument = instrument;
        return wishlist;
    }
}
