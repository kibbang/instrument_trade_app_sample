package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.domain.Instrument;
import hello.instrumenttrade.domain.User;
import hello.instrumenttrade.domain.Wishlist;
import hello.instrumenttrade.domain.WishlistId;
import hello.instrumenttrade.dto.response.WishlistResponse;
import hello.instrumenttrade.repository.UserRepository;
import hello.instrumenttrade.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final InstrumentService instrumentService;

    @Transactional
    public void addWishlist(Long userId, Long instrumentId) {
        WishlistId id = new WishlistId(userId, instrumentId);
        if (wishlistRepository.existsById(id)) {
            throw new InstrumentTradeException(ErrorCode.ALREADY_WISHLISTED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.USER_NOT_FOUND));
        Instrument instrument = instrumentService.findById(instrumentId);
        wishlistRepository.save(Wishlist.of(user, instrument));
    }

    @Transactional
    public void removeWishlist(Long userId, Long instrumentId) {
        WishlistId id = new WishlistId(userId, instrumentId);
        if (!wishlistRepository.existsById(id)) {
            throw new InstrumentTradeException(ErrorCode.WISHLIST_NOT_FOUND);
        }
        wishlistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isWishlisted(Long userId, Long instrumentId) {
        return wishlistRepository.existsById(new WishlistId(userId, instrumentId));
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponse> getMyWishlists(Long userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable)
                .map(WishlistResponse::from);
    }
}
