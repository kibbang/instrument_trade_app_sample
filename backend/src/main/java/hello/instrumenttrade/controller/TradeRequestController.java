package hello.instrumenttrade.controller;

import hello.instrumenttrade.common.ApiResponse;
import hello.instrumenttrade.dto.request.TradeRequestCreateRequest;
import hello.instrumenttrade.dto.response.TradeRequestResponse;
import hello.instrumenttrade.service.TradeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TradeRequestController {

    private final TradeRequestService tradeRequestService;

    // 거래 요청 생성
    @PostMapping("/api/v1/instruments/{instrumentId}/trade-requests")
    public ResponseEntity<ApiResponse<TradeRequestResponse>> create(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long instrumentId,
            @Valid @RequestBody TradeRequestCreateRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(tradeRequestService.createRequest(userId, instrumentId, request)));
    }

    // 거래 수락 (판매자)
    @PatchMapping("/api/v1/trade-requests/{id}/accept")
    public ResponseEntity<ApiResponse<TradeRequestResponse>> accept(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tradeRequestService.accept(userId, id)));
    }

    // 거래 거절 (판매자)
    @PatchMapping("/api/v1/trade-requests/{id}/reject")
    public ResponseEntity<ApiResponse<TradeRequestResponse>> reject(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tradeRequestService.reject(userId, id)));
    }

    // 거래 완료 (판매자)
    @PatchMapping("/api/v1/trade-requests/{id}/complete")
    public ResponseEntity<ApiResponse<TradeRequestResponse>> complete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tradeRequestService.complete(userId, id)));
    }

    // 거래 취소 (구매자)
    @PatchMapping("/api/v1/trade-requests/{id}/cancel")
    public ResponseEntity<ApiResponse<TradeRequestResponse>> cancel(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tradeRequestService.cancel(userId, id)));
    }

    // 내가 보낸 거래 요청 목록 (구매자)
    @GetMapping("/api/v1/users/me/trade-requests/sent")
    public ResponseEntity<ApiResponse<Page<TradeRequestResponse>>> getSentRequests(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tradeRequestService.getMyBuyRequests(userId, pageable)));
    }

    // 받은 거래 요청 목록 (판매자)
    @GetMapping("/api/v1/users/me/trade-requests/received")
    public ResponseEntity<ApiResponse<Page<TradeRequestResponse>>> getReceivedRequests(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tradeRequestService.getMySellRequests(userId, pageable)));
    }
}
