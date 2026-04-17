package hello.instrumenttrade.controller;

import hello.instrumenttrade.common.ApiResponse;
import hello.instrumenttrade.domain.Instrument;
import hello.instrumenttrade.dto.request.InstrumentCreateRequest;
import hello.instrumenttrade.dto.request.InstrumentUpdateRequest;
import hello.instrumenttrade.dto.request.TradeStatusUpdateRequest;
import hello.instrumenttrade.dto.response.InstrumentDetailResponse;
import hello.instrumenttrade.dto.response.InstrumentImageResponse;
import hello.instrumenttrade.dto.response.InstrumentMyStatusResponse;
import hello.instrumenttrade.dto.response.InstrumentSummaryResponse;
import hello.instrumenttrade.dto.search.InstrumentSearchCondition;
import hello.instrumenttrade.service.InstrumentImageService;
import hello.instrumenttrade.service.InstrumentService;
import hello.instrumenttrade.service.TradeRequestService;
import hello.instrumenttrade.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;
    private final InstrumentImageService imageService;
    private final WishlistService wishlistService;
    private final TradeRequestService tradeRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InstrumentSummaryResponse>>> search(
            InstrumentSearchCondition condition,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(instrumentService.search(condition, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstrumentDetailResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody InstrumentCreateRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(instrumentService.create(userId, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstrumentDetailResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(instrumentService.getDetail(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InstrumentDetailResponse>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody InstrumentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(instrumentService.update(userId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        instrumentService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InstrumentDetailResponse>> updateStatus(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody TradeStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(instrumentService.updateStatus(userId, id, request)));
    }

    // 로그인 사용자 기준 찜 여부 + 거래요청 여부 조회
    @GetMapping("/{id}/my-status")
    public ResponseEntity<ApiResponse<InstrumentMyStatusResponse>> getMyStatus(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        boolean wishlisted = userId != null && wishlistService.isWishlisted(userId, id);
        boolean alreadyRequested = userId != null && tradeRequestService.hasActiveRequest(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(new InstrumentMyStatusResponse(wishlisted, alreadyRequested)));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<InstrumentImageResponse>>> uploadImages(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> files) {
        Instrument instrument = instrumentService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(imageService.uploadImages(instrument, files)));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @PathVariable Long imageId) {
        imageService.deleteImage(imageId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{id}/wishlist")
    public ResponseEntity<ApiResponse<Void>> addWishlist(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        wishlistService.addWishlist(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{id}/wishlist")
    public ResponseEntity<ApiResponse<Void>> removeWishlist(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        wishlistService.removeWishlist(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
