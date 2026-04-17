package hello.instrumenttrade.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다."),

    // Instrument
    INSTRUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    FORBIDDEN_INSTRUMENT(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다."),

    // Image
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

    // Wishlist
    ALREADY_WISHLISTED(HttpStatus.CONFLICT, "이미 찜한 게시글입니다."),
    WISHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "찜 목록에 없는 게시글입니다."),

    // Trade
    TRADE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "거래 요청을 찾을 수 없습니다."),
    TRADE_REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN, "거래 요청에 대한 권한이 없습니다."),
    TRADE_ALREADY_REQUESTED(HttpStatus.CONFLICT, "이미 거래 요청한 상품입니다."),
    TRADE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "거래 가능한 상품이 아닙니다."),
    TRADE_INVALID_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 거래 상태 변경입니다."),
    CANNOT_TRADE_OWN_INSTRUMENT(HttpStatus.BAD_REQUEST, "본인의 상품에는 거래 요청할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
