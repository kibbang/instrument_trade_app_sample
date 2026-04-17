package hello.instrumenttrade.domain;

import hello.instrumenttrade.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "instrument_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstrumentImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    private Boolean isThumbnail = false;
    private Integer sortOrder = 0;

    public static InstrumentImage of(Instrument instrument, String imageUrl,
                                      Boolean isThumbnail, Integer sortOrder) {
        InstrumentImage image = new InstrumentImage();
        image.instrument = instrument;
        image.imageUrl = imageUrl;
        image.isThumbnail = isThumbnail;
        image.sortOrder = sortOrder;
        return image;
    }
}
