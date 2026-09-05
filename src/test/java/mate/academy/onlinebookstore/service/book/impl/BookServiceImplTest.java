package mate.academy.onlinebookstore.service.book.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.onlinebookstore.dto.book.BookSearchParametersDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder specificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
            Check if valid book was save
            """)
    public void save_ValidBook_ShouldReturnBookDto() {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto(
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book bookModel = new Book();
        bookModel.setTitle("Kobzar");
        bookModel.setAuthor("Taras Shevchenko");
        bookModel.setIsbn("23042343");
        bookModel.setPrice(BigDecimal.ONE);
        bookModel.setDescription("Book by Taras Shevchenko");
        bookModel.setCoverImage("Image");
        bookModel.setCategories(categories);
        bookModel.setDeleted(false);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Kobzar");
        savedBook.setAuthor("Taras Shevchenko");
        savedBook.setIsbn("23042343");
        savedBook.setPrice(BigDecimal.ONE);
        savedBook.setDescription("Book by Taras Shevchenko");
        savedBook.setCoverImage("Image");
        savedBook.setCategories(categories);
        savedBook.setDeleted(false);

        BookDto bookDto = new BookDto(
                1L,
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        when(bookMapper.toModel(bookRequestDto)).thenReturn(bookModel);
        when(bookRepository.save(bookModel)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(bookDto);

        BookDto actual = bookService.save(bookRequestDto);
        assertNotNull(actual);
        assertEquals(bookDto, actual);

        verify(bookMapper, times(1)).toModel(bookRequestDto);
        verify(bookRepository, times(1)).save(bookModel);
        verify(bookMapper, times(1)).toDto(savedBook);

        verifyNoMoreInteractions(bookMapper, bookRepository);
        verifyNoInteractions(specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if one book was returned as Page
            """)
    public void findAll_WithOneBookInDb_ShouldReturnPageOfOneBookDto() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Story");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Kobzar");
        book.setAuthor("Taras Shevchenko");
        book.setCategories(categories);
        book.setIsbn("23042343");
        book.setPrice(BigDecimal.ONE);
        book.setDescription("Book by Taras Shevchenko");
        book.setCoverImage("Image");
        book.setDeleted(false);

        BookDto bookDto = new BookDto(
                1L,
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        List<Book> books = new ArrayList<>();
        books.add(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actual = bookService.findAll(pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(bookDto, actual.getContent().get(0));

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
        verifyNoInteractions(specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if Page with empty content was returned
            """)
    public void findAll_EmptyDb_ShouldReturnPageWithNoContent() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<Book> bookPage = Page.empty(pageable);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookDto> actual = bookService.findAll(pageable);

        assertTrue(actual.isEmpty());
        assertEquals(0, actual.getTotalElements());

        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper, specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if book was returned by id
            """)
    public void findById_ValidId_ShouldReturnBookDto() {
        Long bookId = 2L;
        Category category = new Category();
        category.setId(1L);
        category.setName("Story");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Kobzar");
        book.setAuthor("Taras Shevchenko");
        book.setCategories(categories);
        book.setIsbn("23042343");
        book.setPrice(BigDecimal.ONE);
        book.setDescription("Book by Taras Shevchenko");
        book.setCoverImage("Image");
        book.setDeleted(false);

        BookDto bookDto = new BookDto(
                bookId,
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.findById(bookId);

        assertNotNull(actual);
        assertEquals(bookDto, actual);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).toDto(book);

        verifyNoMoreInteractions(bookRepository, bookMapper);
        verifyNoInteractions(specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if exception threw when not valid id
            """)
    public void findById_NotValidId_ShouldThrowException() {
        Long id = 32L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(id)
        );

        String expected = "Can't find book by id " + id;
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper, specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if method done
            """)
    public void deleteById_ValidId_ShouldDone() {
        Long id = 1L;

        bookService.deleteById(id);

        verify(bookRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper, specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if exception was threw if invalid id
            """)
    public void update_InvalidId_ShouldThrowException() {
        Long id = 33L;

        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto(
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(id, bookRequestDto)
        );
        String expected = "Can't find book by id " + id;
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(bookMapper, specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if book was updated
            """)
    public void update_ValidData_ShouldReturnUpdatedBookDto() {
        Long id = 1L;

        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto(
                "Kobzar Remake",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.TEN,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        Category category = new Category();
        category.setId(1L);
        category.setName("Story");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Kobzar");
        existingBook.setAuthor("Taras Shevchenko");
        existingBook.setCategories(categories);
        existingBook.setIsbn("23042343");
        existingBook.setPrice(BigDecimal.ONE);
        existingBook.setDescription("Book by Taras Shevchenko");
        existingBook.setCoverImage("Image");
        existingBook.setDeleted(false);

        BookDto bookDto = new BookDto(
                1L,
                "Kobzar Remake",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.TEN,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));

        doAnswer(invocation -> {
            CreateBookRequestDto sourceDto = invocation.getArgument(0);
            Book targetBook = invocation.getArgument(1);

            targetBook.setTitle(sourceDto.title());
            targetBook.setPrice(sourceDto.price());

            return null;
        }).when(bookMapper).updateBookFromDto(bookRequestDto, existingBook);

        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        when(bookMapper.toDto(existingBook)).thenReturn(bookDto);

        BookDto actual = bookService.update(id, bookRequestDto);

        assertNotNull(actual);
        assertEquals(bookDto, actual);

        verify(bookRepository, times(1)).findById(id);
        verify(bookMapper, times(1)).updateBookFromDto(bookRequestDto, existingBook);
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookMapper, times(1)).toDto(existingBook);

        verifyNoMoreInteractions(bookRepository, bookMapper);
        verifyNoInteractions(specificationBuilder);
    }

    @Test
    @DisplayName("""
            Check if Page of books was returned by search parameters
            """)
    public void search_AuthorParam_ShouldReturnOneBook() {
        BookSearchParametersDto bookSearchParametersDto =
                new BookSearchParametersDto(
                        null,
                        new String[]{"Taras Shevchenko"},
                        null);
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        @SuppressWarnings("unchecked")
        Specification<Book> specification = mock(Specification.class);

        Category category = new Category();
        category.setId(1L);
        category.setName("Story");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Kobzar");
        book.setAuthor("Taras Shevchenko");
        book.setCategories(categories);
        book.setIsbn("23042343");
        book.setPrice(BigDecimal.ONE);
        book.setDescription("Book by Taras Shevchenko");
        book.setCoverImage("Image");
        book.setDeleted(false);

        BookDto bookDto = new BookDto(
                2L,
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image",
                List.of(1L));

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(specificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actual = bookService.search(bookSearchParametersDto, pageable);

        assertTrue(actual.getTotalElements() > 0);
        assertEquals(bookDto, actual.getContent().get(0));

        verify(specificationBuilder, times(1)).build(bookSearchParametersDto);
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(1)).toDto(book);

        verifyNoMoreInteractions(specificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Check if Page of books was returned by Category
            """)
    public void findByCategoryId_ValidCategory_ShouldReturnPageOfBooks() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        Category category = new Category();
        category.setId(1L);
        category.setName("Story");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Kobzar");
        book.setAuthor("Taras Shevchenko");
        book.setCategories(categories);
        book.setIsbn("23042343");
        book.setPrice(BigDecimal.ONE);
        book.setDescription("Book by Taras Shevchenko");
        book.setCoverImage("Image");
        book.setDeleted(false);

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                2L,
                "Kobzar",
                "Taras Shevchenko",
                "23042343",
                BigDecimal.ONE,
                "Book by Taras Shevchenko",
                "Image");

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAllByCategoriesId(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategoryIds(book)).thenReturn(bookDto);

        Page<BookDtoWithoutCategoryIds> actual = bookService.findByCategoryId(categoryId, pageable);

        assertTrue(actual.getTotalElements() > 0);
        assertEquals(bookDto, actual.getContent().get(0));

        verify(bookRepository, times(1)).findAllByCategoriesId(categoryId, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategoryIds(book);

        verifyNoMoreInteractions(bookRepository, bookMapper);
        verifyNoInteractions(specificationBuilder);
    }
}
