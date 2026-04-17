package hello.instrumenttrade.repository;

import hello.instrumenttrade.domain.InstrumentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstrumentImageRepository extends JpaRepository<InstrumentImage, Long> {
    List<InstrumentImage> findByInstrumentId(Long instrumentId);
}
