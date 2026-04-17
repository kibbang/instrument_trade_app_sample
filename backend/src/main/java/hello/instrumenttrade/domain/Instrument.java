package hello.instrumenttrade.domain;

import hello.instrumenttrade.common.BaseTimeEntity;
import hello.instrumenttrade.enums.TradeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "instruments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Instrument extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private InstrumentCategory category;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private Integer price;

    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status = TradeStatus.SELLING;

    @Column(nullable = false)
    private Integer viewCount = 0;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<InstrumentImage> images = new ArrayList<>();

    public static Instrument of(User user, InstrumentCategory category,
                                 String title, String description,
                                 Integer price, String region) {
        Instrument instrument = new Instrument();
        instrument.user = user;
        instrument.category = category;
        instrument.title = title;
        instrument.description = description;
        instrument.price = price;
        instrument.region = region;
        return instrument;
    }

    public void update(InstrumentCategory category, String title,
                       String description, Integer price, String region) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.region = region;
    }

    public void changeStatus(TradeStatus status) {
        this.status = status;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
