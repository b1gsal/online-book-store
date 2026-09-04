package mate.academy.onlinebookstore.service.category.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.category.CategoryDto;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
            Check if valid category was saved
            """)
    public void save_validData_ShouldSaveAndReturnCategoryDto() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Action", "Movie that has fight");

        Category categoryFromDto = new Category();
        categoryFromDto.setName("Action");
        categoryFromDto.setDescription("Movie that has fight");
        categoryFromDto.setDeleted(false);

        Category categorySaved = new Category();
        categorySaved.setId(1L);
        categorySaved.setName("Action");
        categorySaved.setDescription("Movie that has fight");
        categorySaved.setDeleted(false);

        CategoryDto categoryDto = new CategoryDto(1L, "Action", "Movie that has fight");

        Mockito.when(categoryMapper.toModel(categoryRequestDto)).thenReturn(categoryFromDto);
        Mockito.when(categoryRepository.save(categoryFromDto)).thenReturn(categorySaved);
        Mockito.when(categoryMapper.toDto(categorySaved)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(categoryRequestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(categoryDto, actual);

        Mockito.verify(categoryMapper, Mockito.times(1)).toModel(categoryRequestDto);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(categoryFromDto);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(categorySaved);

        Mockito.verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("""
            Check if Page of category dtos was returned
            """)
    public void findAll_TwoCategory_ShouldReturnPageOfTwoCategoryDto() {
        Category categoryAction = new Category();
        categoryAction.setId(1L);
        categoryAction.setName("Action");
        categoryAction.setDescription("Movie that has fight");
        categoryAction.setDeleted(false);

        Category categoryAdventure = new Category();
        categoryAdventure.setId(2L);
        categoryAdventure.setName("Adventure");
        categoryAdventure.setDescription("Movie about adventure");
        categoryAdventure.setDeleted(false);

        List<Category> categories = new ArrayList<>();
        categories.add(categoryAction);
        categories.add(categoryAdventure);

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());



        CategoryDto categoryActionDto = new CategoryDto(1L, "Action", "Movie that has fight");
        CategoryDto categoryAdventureDto = new CategoryDto(2L, "Adventure", "Movie about adventure");


        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(categoryAction)).thenReturn(categoryActionDto);
        Mockito.when(categoryMapper.toDto(categoryAdventure)).thenReturn(categoryAdventureDto);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        Assertions.assertEquals(2, actual.getTotalElements());
        Assertions.assertEquals(categoryActionDto, actual.getContent().get(0));
        Assertions.assertEquals(categoryAdventureDto, actual.getContent().get(1));

        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(categoryAction);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(categoryAdventure);
    }

    @Test
    @DisplayName("""
            Check if exception was threw if invalid id
            """)
    public void findById_InvalidId_ShouldTrowException() {
        Long id = 14L;

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> categoryService.findById(id)
        );

        String expected = "Can't find category by id " + id;
        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(id);

        Mockito.verifyNoMoreInteractions(categoryRepository);
        Mockito.verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("""
            Check if Category Dto was returned by valid id
            """)
    public void findById_ValidId_ShouldReturnCategoryDto() {
        Long id = 1L;
        Category categoryAction = new Category();
        categoryAction.setId(id);
        categoryAction.setName("Action");
        categoryAction.setDescription("Movie that has fight");
        categoryAction.setDeleted(false);

        CategoryDto categoryActionDto = new CategoryDto(id, "Action", "Movie that has fight");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryAction));
        Mockito.when(categoryMapper.toDto(categoryAction)).thenReturn(categoryActionDto);

        CategoryDto actual = categoryService.findById(id);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(categoryActionDto, actual);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(id);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(categoryAction);

        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Check if exception was trew if invalid id
            """)
    public void update_InvalidId_ShouldThrowException() {
        Long id = 21L;

        CategoryRequestDto categoryRequestDto = null;

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> categoryService.update(id, categoryRequestDto)
        );

        String expected = "Can't find category by id " + id;

        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(id);

        Mockito.verifyNoMoreInteractions(categoryRepository);
        Mockito.verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("""
            Check if category dto was updated and returned
            """)
    public void update_ValidData_ShouldReturnUpdatedCategoryDto() {
        Long id = 13L;
        CategoryRequestDto requestDto = new CategoryRequestDto("Action", "Action movie");

        Category category = new Category();
        category.setId(id);
        category.setName("Action");
        category.setDescription("movie");
        category.setDeleted(false);

        CategoryDto categoryDto = new CategoryDto(id, "Action", "Action movie");

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        Mockito.doAnswer(invocation -> {
            CategoryRequestDto sourceDto = invocation.getArgument(0);
            Category target = invocation.getArgument(1);

            target.setDescription(sourceDto.description());

            return null;
        }).when(categoryMapper).updateCategoryFromDto(requestDto, category);

        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.update(id, requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(categoryDto, actual);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(id);
        Mockito.verify(categoryMapper, Mockito.times(1)).updateCategoryFromDto(requestDto, category);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(category);

        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Check if delete done
            """)
    public void deleteById_validId_ShouldDone() {
        Long id = 1L;

        categoryService.deleteById(id);

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(id);
        Mockito.verifyNoMoreInteractions(categoryRepository);
        Mockito.verifyNoInteractions(categoryMapper);
    }
}
