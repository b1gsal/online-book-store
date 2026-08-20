package mate.academy.onlinebookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.URL;

public record CreateBookRequestDto(
        @NotBlank
        @Size(max = 255)
        String title,
        @NotBlank
        @Size(max = 255)
        String author,
        @NotBlank
        @ISBN
        String isbn,
        @Positive
        BigDecimal price,
        @Size(max = 255)
        String description,
        @URL
        String coverImage,
        List<Long> categoryIds) {
}
