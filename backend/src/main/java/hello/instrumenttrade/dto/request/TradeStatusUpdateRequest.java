package hello.instrumenttrade.dto.request;

import hello.instrumenttrade.enums.TradeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TradeStatusUpdateRequest {

    @NotNull(message = "거래 상태는 필수입니다.")
    private TradeStatus status;
}
