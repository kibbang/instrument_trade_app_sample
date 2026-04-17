package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.InstrumentCategory;
import lombok.Getter;

@Getter
public class CategoryResponse {
    private final Long id;
    private final String name;

    private CategoryResponse(InstrumentCategory category) {
        this.id = category.getId();
        this.name = category.getName().name();
    }

    public static CategoryResponse from(InstrumentCategory category) {
        return new CategoryResponse(category);
    }
}
