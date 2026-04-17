package hello.instrumenttrade.controller;

import hello.instrumenttrade.common.ApiResponse;
import hello.instrumenttrade.dto.request.PasswordChangeRequest;
import hello.instrumenttrade.dto.request.UserProfileUpdateRequest;
import hello.instrumenttrade.dto.response.InstrumentSummaryResponse;
import hello.instrumenttrade.dto.response.UserProfileResponse;
import hello.instrumenttrade.dto.response.UserResponse;
import hello.instrumenttrade.dto.response.WishlistResponse;
import hello.instrumenttrade.service.InstrumentService;
import hello.instrumenttrade.service.UserService;
import hello.instrumenttrade.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final InstrumentService instrumentService;
    private final WishlistService wishlistService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateProfile(userId, request)));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(userId)));
    }

    @GetMapping("/me/instruments")
    public ResponseEntity<ApiResponse<Page<InstrumentSummaryResponse>>> getMyInstruments(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(instrumentService.getMyInstruments(userId, pageable)));
    }

    @GetMapping("/me/wishlists")
    public ResponseEntity<ApiResponse<Page<WishlistResponse>>> getMyWishlists(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(wishlistService.getMyWishlists(userId, pageable)));
    }
}
