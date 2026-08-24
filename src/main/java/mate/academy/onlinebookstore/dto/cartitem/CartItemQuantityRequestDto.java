package mate.academy.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record CartItemQuantityRequestDto(
        @Positive
        int quantity) {
}
