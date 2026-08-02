package mate.academy.onlinebookstore.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.BookSearchParametersDto;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.repository.SpecificationBuilder;
import mate.academy.onlinebookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String ISBN = "isbn";
    private final SpecificationProviderManager<Book> providerManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> bookSpecification = (
                (root, query, criteriaBuilder) -> null);
        if (searchParametersDto.titles() != null && searchParametersDto.titles().length > 0) {
            bookSpecification = bookSpecification
                    .and(providerManager.getSpecificationProvider(TITLE)
                    .getSpecification(searchParametersDto.titles()));
        }
        if (searchParametersDto.authors() != null && searchParametersDto.authors().length > 0) {
            bookSpecification = bookSpecification
                    .and(providerManager.getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParametersDto.authors()));
        }
        if (searchParametersDto.isbns() != null && searchParametersDto.isbns().length > 0) {
            bookSpecification = bookSpecification
                    .and(providerManager.getSpecificationProvider(ISBN)
                    .getSpecification(searchParametersDto.isbns()));
        }
        return bookSpecification;
    }
}
