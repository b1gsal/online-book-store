package mate.academy.onlinebookstore.service.shoppingcart;

import mate.academy.onlinebookstore.dto.cartitem.CartItemQuantityRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto addBooks(CartItemRequestDto cartItemRequestDto, Authentication authentication);

    ShoppingCartDto getShoppingCart(Authentication authentication);

    ShoppingCartDto update(
            Long cartItemId,
            CartItemQuantityRequestDto cartItemQuantityRequestDto,
            Authentication authentication);

    void delete(Long cartItemId, Authentication authentication);

    void createUserShoppingCart(User user);
}
