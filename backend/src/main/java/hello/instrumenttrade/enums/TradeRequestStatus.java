package hello.instrumenttrade.enums;

public enum TradeRequestStatus {
    REQUESTED,   // 구매자가 거래 요청
    ACCEPTED,    // 판매자가 수락 → 악기 RESERVED
    REJECTED,    // 판매자가 거절
    COMPLETED,   // 거래 완료 → 악기 SOLD
    CANCELLED    // 구매자가 취소
}
