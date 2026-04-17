package hello.instrumenttrade.dto.search;

import hello.instrumenttrade.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstrumentSearchCondition {
    private String keyword;
    private Long categoryId;
    private Integer minPrice;
    private Integer maxPrice;
    private String region;
    private TradeStatus status = TradeStatus.SELLING;
}
