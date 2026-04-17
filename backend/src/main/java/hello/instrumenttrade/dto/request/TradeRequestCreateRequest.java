package hello.instrumenttrade.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TradeRequestCreateRequest {

    @Size(max = 500, message = "메시지는 500자 이내로 입력해주세요.")
    private String message;
}
