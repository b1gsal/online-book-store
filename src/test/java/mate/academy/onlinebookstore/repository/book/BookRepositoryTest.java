package mate.academy.onlinebookstore.repository.book;

import java.util.Set;
import mate.academy.onlinebookstore.config.AbstractTestContainers;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest extends AbstractTestContainers {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Check if Page of books was returned by category
            """)
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllByCategoriesId_ExistCategory_ShouldReturnPageOfTwoBooksWithActionCategory() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> actual = bookRepository.findAllByCategoriesId(categoryId, pageable);

        Assertions.assertEquals(2, actual.getTotalElements());

        Set<Category> firstBookCategories = actual.getContent().get(0).getCategories();
        boolean hasCategoryFirstBook = firstBookCategories.stream().anyMatch(
                category -> category.getId().equals(categoryId));
        Assertions.assertTrue(hasCategoryFirstBook);
        Set<Category> secondBookCategories = actual.getContent().get(1).getCategories();
        boolean hasCategorySecondBook = secondBookCategories.stream().anyMatch(
                category -> category.getId().equals(categoryId));
        Assertions.assertTrue(hasCategorySecondBook);

    }

}