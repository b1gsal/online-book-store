package mate.academy.onlinebookstore.service.category;

import mate.academy.onlinebookstore.dto.category.CategoryDto;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto save(CategoryRequestDto requestDto);

    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto update(Long id, CategoryRequestDto requestDto);

    void deleteById(Long id);
}
