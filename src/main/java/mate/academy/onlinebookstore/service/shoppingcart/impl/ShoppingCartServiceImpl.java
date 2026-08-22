package mate.academy.onlinebookstore.service.shoppingcart.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.cartitem.CartItemQuantityRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CartItemMapper;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.cartitem.CartItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    @Transactional
    @Override
    public ShoppingCartDto addBooks(CartItemRequestDto cartItemRequestDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartByUserId(user.getId());
        Book book = bookRepository.findById(cartItemRequestDto.bookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find book by id " + cartItemRequestDto.bookId()));

        Set<CartItem> cartItems = shoppingCart.getCartItems();
        List<Book> list = cartItems.stream().map(CartItem::getBook).toList();
        if (list.contains(book)) {
            cartItems.stream().forEach(cartItem -> {
                if (cartItem.getBook().equals(book)) {
                    int quantity = cartItem.getQuantity() + cartItemRequestDto.quantity();
                    cartItem.setQuantity(quantity);
                }
            });
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setBook(book);
            cartItem.setQuantity(cartItemRequestDto.quantity());
            CartItem savedCartItem = cartItemRepository.save(cartItem);
            shoppingCart.getCartItems().add(savedCartItem);
        }

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCartByUserId = shoppingCartRepository
                .getShoppingCartByUserId(user.getId());
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    @Transactional
    @Override
    public ShoppingCartDto update(
            Long cartItemId,
            CartItemQuantityRequestDto cartItemQuantityRequestDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCartByUserId = shoppingCartRepository
                .getShoppingCartByUserId(user.getId());

        for (CartItem cartItem : shoppingCartByUserId.getCartItems()) {
            if (Objects.equals(cartItem.getId(), cartItemId)) {
                cartItem.setQuantity(cartItemQuantityRequestDto.quantity());
                break;
            }
        }

        return shoppingCartMapper.toDto(
                shoppingCartRepository.save(shoppingCartByUserId));
    }

    @Transactional
    @Override
    public void delete(Long cartItemId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCartByUserId = shoppingCartRepository
                .getShoppingCartByUserId(user.getId());
        for (CartItem cartItem : shoppingCartByUserId.getCartItems()) {
            if (Objects.equals(cartItem.getId(), cartItemId)) {
                cartItemRepository.deleteById(cartItemId);
                break;
            }
        }
    }
}
