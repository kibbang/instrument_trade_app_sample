package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.domain.Instrument;
import hello.instrumenttrade.domain.TradeRequest;
import hello.instrumenttrade.domain.User;
import hello.instrumenttrade.dto.request.TradeRequestCreateRequest;
import hello.instrumenttrade.dto.response.TradeRequestResponse;
import hello.instrumenttrade.enums.TradeRequestStatus;
import hello.instrumenttrade.enums.TradeStatus;
import hello.instrumenttrade.repository.InstrumentRepository;
import hello.instrumenttrade.repository.TradeRequestRepository;
import hello.instrumenttrade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeRequestService {

    private final TradeRequestRepository tradeRequestRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserRepository userRepository;

    @Transactional
    public TradeRequestResponse createRequest(Long buyerId, Long instrumentId,
                                               TradeRequestCreateRequest request) {
        User buyer = findUser(buyerId);
        Instrument instrument = findInstrument(instrumentId);

        if (instrument.getUser().getId().equals(buyerId)) {
            throw new InstrumentTradeException(ErrorCode.CANNOT_TRADE_OWN_INSTRUMENT);
        }
        if (instrument.getStatus() != TradeStatus.SELLING) {
            throw new InstrumentTradeException(ErrorCode.TRADE_NOT_AVAILABLE);
        }
        if (tradeRequestRepository.existsByInstrumentIdAndBuyerIdAndStatusIn(
                instrumentId, buyerId, List.of(TradeRequestStatus.REQUESTED, TradeRequestStatus.ACCEPTED))) {
            throw new InstrumentTradeException(ErrorCode.TRADE_ALREADY_REQUESTED);
        }

        TradeRequest tradeRequest = TradeRequest.of(instrument, buyer, instrument.getUser(),
                request.getMessage());
        return TradeRequestResponse.from(tradeRequestRepository.save(tradeRequest));
    }

    @Transactional
    public TradeRequestResponse accept(Long sellerId, Long tradeRequestId) {
        TradeRequest tr = findByIdAndSeller(tradeRequestId, sellerId);
        if (tr.getStatus() != TradeRequestStatus.REQUESTED) {
            throw new InstrumentTradeException(ErrorCode.TRADE_INVALID_STATUS);
        }
        tr.accept();
        tr.getInstrument().changeStatus(TradeStatus.RESERVED);
        return TradeRequestResponse.from(tr);
    }

    @Transactional
    public TradeRequestResponse reject(Long sellerId, Long tradeRequestId) {
        TradeRequest tr = findByIdAndSeller(tradeRequestId, sellerId);
        if (tr.getStatus() != TradeRequestStatus.REQUESTED) {
            throw new InstrumentTradeException(ErrorCode.TRADE_INVALID_STATUS);
        }
        tr.reject();
        return TradeRequestResponse.from(tr);
    }

    @Transactional
    public TradeRequestResponse complete(Long sellerId, Long tradeRequestId) {
        TradeRequest tr = findByIdAndSeller(tradeRequestId, sellerId);
        if (tr.getStatus() != TradeRequestStatus.ACCEPTED) {
            throw new InstrumentTradeException(ErrorCode.TRADE_INVALID_STATUS);
        }
        tr.complete();
        tr.getInstrument().changeStatus(TradeStatus.SOLD);
        return TradeRequestResponse.from(tr);
    }

    @Transactional
    public TradeRequestResponse cancel(Long buyerId, Long tradeRequestId) {
        TradeRequest tr = findById(tradeRequestId);
        if (!tr.getBuyer().getId().equals(buyerId)) {
            throw new InstrumentTradeException(ErrorCode.TRADE_REQUEST_FORBIDDEN);
        }
        if (tr.getStatus() != TradeRequestStatus.REQUESTED) {
            throw new InstrumentTradeException(ErrorCode.TRADE_INVALID_STATUS);
        }
        tr.cancel();
        return TradeRequestResponse.from(tr);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveRequest(Long buyerId, Long instrumentId) {
        return tradeRequestRepository.existsByInstrumentIdAndBuyerIdAndStatusIn(
                instrumentId, buyerId,
                List.of(TradeRequestStatus.REQUESTED, TradeRequestStatus.ACCEPTED));
    }

    @Transactional(readOnly = true)
    public Page<TradeRequestResponse> getMyBuyRequests(Long userId, Pageable pageable) {
        return tradeRequestRepository.findByBuyerId(userId, pageable)
                .map(TradeRequestResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TradeRequestResponse> getMySellRequests(Long userId, Pageable pageable) {
        return tradeRequestRepository.findBySellerId(userId, pageable)
                .map(TradeRequestResponse::from);
    }

    private TradeRequest findById(Long id) {
        return tradeRequestRepository.findById(id)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.TRADE_REQUEST_NOT_FOUND));
    }

    private TradeRequest findByIdAndSeller(Long id, Long sellerId) {
        TradeRequest tr = findById(id);
        if (!tr.getSeller().getId().equals(sellerId)) {
            throw new InstrumentTradeException(ErrorCode.TRADE_REQUEST_FORBIDDEN);
        }
        return tr;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.USER_NOT_FOUND));
    }

    private Instrument findInstrument(Long instrumentId) {
        return instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.INSTRUMENT_NOT_FOUND));
    }
}
