package hello.instrumenttrade.domain;

import hello.instrumenttrade.common.BaseTimeEntity;
import hello.instrumenttrade.enums.TradeRequestStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "trade_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeRequestStatus status = TradeRequestStatus.REQUESTED;

    @Column(length = 500)
    private String message;

    public static TradeRequest of(Instrument instrument, User buyer, User seller, String message) {
        TradeRequest tr = new TradeRequest();
        tr.instrument = instrument;
        tr.buyer = buyer;
        tr.seller = seller;
        tr.message = message;
        return tr;
    }

    public void accept() {
        this.status = TradeRequestStatus.ACCEPTED;
    }

    public void reject() {
        this.status = TradeRequestStatus.REJECTED;
    }

    public void complete() {
        this.status = TradeRequestStatus.COMPLETED;
    }

    public void cancel() {
        this.status = TradeRequestStatus.CANCELLED;
    }
}
