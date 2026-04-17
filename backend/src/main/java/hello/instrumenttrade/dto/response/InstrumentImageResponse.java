package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.InstrumentImage;
import lombok.Getter;

@Getter
public class InstrumentImageResponse {
    private final Long id;
    private final String imageUrl;
    private final Boolean isThumbnail;
    private final Integer sortOrder;

    private InstrumentImageResponse(InstrumentImage image) {
        this.id = image.getId();
        this.imageUrl = image.getImageUrl();
        this.isThumbnail = image.getIsThumbnail();
        this.sortOrder = image.getSortOrder();
    }

    public static InstrumentImageResponse from(InstrumentImage image) {
        return new InstrumentImageResponse(image);
    }
}
