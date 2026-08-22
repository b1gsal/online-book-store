package mate.academy.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemQuantityRequestDto(
        @NotNull
        @Positive
        Integer quantity) {
}
