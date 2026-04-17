package hello.instrumenttrade.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstrumentMyStatusResponse {
    private final boolean wishlisted;
    private final boolean alreadyRequested;
}
