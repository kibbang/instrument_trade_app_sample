package hello.instrumenttrade.repository;

import hello.instrumenttrade.dto.response.InstrumentSummaryResponse;
import hello.instrumenttrade.dto.search.InstrumentSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstrumentCustomRepository {
    Page<InstrumentSummaryResponse> search(InstrumentSearchCondition condition, Pageable pageable);
}
