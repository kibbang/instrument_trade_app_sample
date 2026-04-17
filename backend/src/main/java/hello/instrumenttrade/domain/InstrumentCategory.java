package hello.instrumenttrade.domain;

import hello.instrumenttrade.enums.InstrumentCategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "instrument_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstrumentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private InstrumentCategoryType name;
}
