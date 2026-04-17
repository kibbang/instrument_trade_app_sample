package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.domain.Instrument;
import hello.instrumenttrade.domain.InstrumentCategory;
import hello.instrumenttrade.domain.User;
import hello.instrumenttrade.dto.request.InstrumentCreateRequest;
import hello.instrumenttrade.dto.request.InstrumentUpdateRequest;
import hello.instrumenttrade.dto.request.TradeStatusUpdateRequest;
import hello.instrumenttrade.dto.response.InstrumentDetailResponse;
import hello.instrumenttrade.dto.response.InstrumentSummaryResponse;
import hello.instrumenttrade.dto.search.InstrumentSearchCondition;
import hello.instrumenttrade.repository.InstrumentRepository;
import hello.instrumenttrade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public Page<InstrumentSummaryResponse> search(InstrumentSearchCondition condition, Pageable pageable) {
        return instrumentRepository.search(condition, pageable);
    }

    @Transactional
    public InstrumentDetailResponse create(Long userId, InstrumentCreateRequest request) {
        User user = findUser(userId);
        InstrumentCategory category = categoryService.findById(request.getCategoryId());

        Instrument instrument = Instrument.of(
                user, category,
                request.getTitle(), request.getDescription(),
                request.getPrice(), request.getRegion()
        );
        return InstrumentDetailResponse.from(instrumentRepository.save(instrument));
    }

    @Transactional
    public InstrumentDetailResponse getDetail(Long instrumentId) {
        Instrument instrument = findById(instrumentId);
        instrument.incrementViewCount();
        return InstrumentDetailResponse.from(instrument);
    }

    @Transactional
    public InstrumentDetailResponse update(Long userId, Long instrumentId, InstrumentUpdateRequest request) {
        Instrument instrument = findByIdAndCheckOwner(instrumentId, userId);
        InstrumentCategory category = categoryService.findById(request.getCategoryId());
        instrument.update(category, request.getTitle(), request.getDescription(),
                request.getPrice(), request.getRegion());
        return InstrumentDetailResponse.from(instrument);
    }

    @Transactional
    public void delete(Long userId, Long instrumentId) {
        Instrument instrument = findByIdAndCheckOwner(instrumentId, userId);
        instrument.softDelete();
    }

    @Transactional
    public InstrumentDetailResponse updateStatus(Long userId, Long instrumentId,
                                                  TradeStatusUpdateRequest request) {
        Instrument instrument = findByIdAndCheckOwner(instrumentId, userId);
        instrument.changeStatus(request.getStatus());
        return InstrumentDetailResponse.from(instrument);
    }

    @Transactional(readOnly = true)
    public Page<InstrumentSummaryResponse> getMyInstruments(Long userId, Pageable pageable) {
        return instrumentRepository.findByUserId(userId, pageable)
                .map(instrument -> {
                    InstrumentSearchCondition cond = new InstrumentSearchCondition();
                    return toSummary(instrument);
                });
    }

    public Instrument findById(Long instrumentId) {
        return instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.INSTRUMENT_NOT_FOUND));
    }

    private Instrument findByIdAndCheckOwner(Long instrumentId, Long userId) {
        Instrument instrument = findById(instrumentId);
        if (!instrument.getUser().getId().equals(userId)) {
            throw new InstrumentTradeException(ErrorCode.FORBIDDEN_INSTRUMENT);
        }
        return instrument;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.USER_NOT_FOUND));
    }

    private InstrumentSummaryResponse toSummary(Instrument instrument) {
        String thumbnail = instrument.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(null);

        return new InstrumentSummaryResponse(
                instrument.getId(), instrument.getTitle(), instrument.getPrice(),
                instrument.getRegion(), instrument.getStatus(), instrument.getViewCount(),
                instrument.getCreatedAt(), thumbnail,
                instrument.getCategory().getId(), instrument.getCategory().getName().name(),
                instrument.getUser().getId(), instrument.getUser().getNickname()
        );
    }
}
