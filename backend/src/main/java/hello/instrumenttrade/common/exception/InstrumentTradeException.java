package hello.instrumenttrade.common.exception;

import lombok.Getter;

@Getter
public class InstrumentTradeException extends RuntimeException {

    private final ErrorCode errorCode;

    public InstrumentTradeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
