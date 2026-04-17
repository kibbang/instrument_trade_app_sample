package hello.instrumenttrade.repository;

import hello.instrumenttrade.domain.InstrumentCategory;
import hello.instrumenttrade.enums.InstrumentCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrumentCategoryRepository extends JpaRepository<InstrumentCategory, Long> {
    Optional<InstrumentCategory> findByName(InstrumentCategoryType name);
}
