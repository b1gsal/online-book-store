package mate.academy.onlinebookstore.dto.shoppingcart;

import java.util.Set;
import mate.academy.onlinebookstore.dto.cartitem.CartItemDto;

public record ShoppingCartDto(
        Long id,
        Long userId,
        Set<CartItemDto> cartItems) {
}
