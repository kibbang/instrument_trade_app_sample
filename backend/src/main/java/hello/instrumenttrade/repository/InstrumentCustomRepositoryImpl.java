package hello.instrumenttrade.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.instrumenttrade.domain.QInstrument;
import hello.instrumenttrade.domain.QInstrumentCategory;
import hello.instrumenttrade.domain.QInstrumentImage;
import hello.instrumenttrade.domain.QUser;
import hello.instrumenttrade.dto.response.InstrumentSummaryResponse;
import hello.instrumenttrade.dto.search.InstrumentSearchCondition;
import hello.instrumenttrade.enums.TradeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class InstrumentCustomRepositoryImpl implements InstrumentCustomRepository {

    private final JPAQueryFactory query;

    private static final QInstrument instrument = QInstrument.instrument;
    private static final QInstrumentImage instrumentImage = QInstrumentImage.instrumentImage;
    private static final QInstrumentCategory category = QInstrumentCategory.instrumentCategory;
    private static final QUser user = QUser.user;

    @Override
    public Page<InstrumentSummaryResponse> search(InstrumentSearchCondition cond, Pageable pageable) {
        List<InstrumentSummaryResponse> content = query
                .select(Projections.constructor(InstrumentSummaryResponse.class,
                        instrument.id,
                        instrument.title,
                        instrument.price,
                        instrument.region,
                        instrument.status,
                        instrument.viewCount,
                        instrument.createdAt,
                        instrumentImage.imageUrl.min(),
                        category.id,
                        category.name.stringValue(),
                        user.id,
                        user.nickname
                ))
                .from(instrument)
                .join(instrument.category, category)
                .join(instrument.user, user)
                .leftJoin(instrument.images, instrumentImage).on(instrumentImage.isThumbnail.isTrue())
                .where(
                        keywordLike(cond.getKeyword()),
                        categoryEq(cond.getCategoryId()),
                        priceBetween(cond.getMinPrice(), cond.getMaxPrice()),
                        regionLike(cond.getRegion()),
                        statusEq(cond.getStatus())
                )
                .groupBy(instrument.id, category.id, category.name, user.id, user.nickname)
                .orderBy(sortOrder(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query
                .select(instrument.count())
                .from(instrument)
                .where(
                        keywordLike(cond.getKeyword()),
                        categoryEq(cond.getCategoryId()),
                        priceBetween(cond.getMinPrice(), cond.getMaxPrice()),
                        regionLike(cond.getRegion()),
                        statusEq(cond.getStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression keywordLike(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return instrument.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression categoryEq(Long categoryId) {
        if (categoryId == null) return null;
        return category.id.eq(categoryId);
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) return null;
        if (minPrice == null) return instrument.price.loe(maxPrice);
        if (maxPrice == null) return instrument.price.goe(minPrice);
        return instrument.price.between(minPrice, maxPrice);
    }

    private BooleanExpression regionLike(String region) {
        if (!StringUtils.hasText(region)) return null;
        return instrument.region.containsIgnoreCase(region);
    }

    private BooleanExpression statusEq(TradeStatus status) {
        if (status == null) return null;
        return instrument.status.eq(status);
    }

    private OrderSpecifier<?> sortOrder(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return instrument.createdAt.desc();
        }
        return pageable.getSort().stream()
                .findFirst()
                .map(order -> switch (order.getProperty()) {
                    case "price" -> order.isAscending() ? instrument.price.asc() : instrument.price.desc();
                    case "viewCount" -> order.isAscending() ? instrument.viewCount.asc() : instrument.viewCount.desc();
                    default -> order.isAscending() ? instrument.createdAt.asc() : instrument.createdAt.desc();
                })
                .orElse(instrument.createdAt.desc());
    }
}
