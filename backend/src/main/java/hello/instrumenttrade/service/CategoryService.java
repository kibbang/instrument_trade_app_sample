package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.domain.InstrumentCategory;
import hello.instrumenttrade.dto.response.CategoryResponse;
import hello.instrumenttrade.repository.InstrumentCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final InstrumentCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstrumentCategory findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
