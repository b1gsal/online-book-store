package mate.academy.onlinebookstore.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.academy.onlinebookstore.dto.cartitem.CartItemDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemQuantityRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.ShoppingCartMapper;
import mate.academy.onlinebookstore.model.*;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.cartitem.CartItemRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Check if books were added to shopping cart")
    public void addBooks_ValidData_ShouldReturnUpdatedShoppingCart() {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 3);

        User user = new User();
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setDeleted(false);

        Book bookModel = getBookModel();

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(bookModel);
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);

        CartItemDto cartItemDto = new CartItemDto(1L, bookModel.getId(), bookModel.getTitle(), requestDto.quantity());

        ShoppingCartDto expected = new ShoppingCartDto(1L, 1L, Set.of(cartItemDto));

        when(shoppingCartRepository.getShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.of(bookModel));
        when(cartItemRepository.save(Mockito.any())).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.addBooks(requestDto, authentication);

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(shoppingCart.getCartItems().contains(cartItem));


        verify(authentication, times(1)).getPrincipal();
        verify(shoppingCartRepository, times(1)).getShoppingCartByUserId(user.getId());
        verify(bookRepository, times(1)).findById(requestDto.bookId());
        verify(cartItemRepository, times(1)).save(Mockito.any());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);

        verifyNoMoreInteractions(authentication, shoppingCartRepository, bookRepository, cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Check if exception was threw by non existing book")
    public void addBooks_InvalidBookId_ShouldThrowException() {
        CartItemRequestDto requestDto = new CartItemRequestDto(15L, 3);

        User user = new User();
        user.setId(1L);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.getShoppingCartByUserId(user.getId())).thenReturn(new ShoppingCart());
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addBooks(requestDto, authentication));

        String actual = exception.getMessage();
        String expected = "Can't find book by id " + requestDto.bookId();

        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(requestDto.bookId());
        verify(shoppingCartRepository, times(1)).getShoppingCartByUserId(user.getId());


        verifyNoMoreInteractions(shoppingCartRepository, bookRepository);
        verifyNoInteractions(cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Check if book's quantity were updated in shopping cart")
    public void addBooks_ExistingBooksInCart_ShouldReturnUpdatedShoppingCart() {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 3);

        User user = new User();
        user.setId(1L);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);



        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setDeleted(false);

        Book bookModel = getBookModel();

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);
        cartItem.setBook(bookModel);
        cartItem.setShoppingCart(shoppingCart);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);

        shoppingCart.setCartItems(cartItems);

        CartItemDto cartItemUpdated = new CartItemDto(1L, bookModel.getId(), bookModel.getTitle(), 5);
        Set<CartItemDto> cartItemsUpdated = new HashSet<>();
        cartItemsUpdated.add(cartItemUpdated);

        ShoppingCartDto expected = new ShoppingCartDto(1L, user.getId(), cartItemsUpdated);

        when(shoppingCartRepository.getShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.of(bookModel));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.addBooks(requestDto, authentication);

        assertNotNull(shoppingCart);
        assertEquals(1, shoppingCart.getCartItems().size());
        assertEquals(expected, actual);
        assertEquals(5, cartItem.getQuantity());

        verify(authentication, times(1)).getPrincipal();
        verify(shoppingCartRepository, times(1)).getShoppingCartByUserId(user.getId());
        verify(bookRepository, times(1)).findById(requestDto.bookId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);

        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get shopping cart")
    public void getShoppingCart_ValidAuthentication_ShouldReturnShoppingCart() {
        User user = new User();
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setDeleted(false);
        shoppingCart.setCartItems(new HashSet<>());

        ShoppingCartDto expected = new ShoppingCartDto(1L, user.getId(), new HashSet<>());

        when(shoppingCartRepository.getShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getShoppingCart(authentication);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).getShoppingCartByUserId(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);

        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper, authentication);
        verifyNoInteractions(bookRepository, cartItemRepository);
    }

    @Test
    @DisplayName("Check if new shopping cart was created")
    public void createUserShoppingCart_ValidUser_ShouldReturnNewShoppingCart() {
        User user = new User();
        user.setId(1L);
        user.setEmail("den@gmail.com");

        shoppingCartService.createUserShoppingCart(user);

        ArgumentCaptor<ShoppingCart> cartCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository, times(1)).save(cartCaptor.capture());

        ShoppingCart captorValue = cartCaptor.getValue();

        assertNotNull(captorValue);
        assertEquals(user, captorValue.getUser());

        verifyNoMoreInteractions(shoppingCartRepository);
        verifyNoInteractions(shoppingCartMapper, cartItemRepository, bookRepository);
    }

    @Test
    @DisplayName("Check if cart item was deleted")
    public void delete_ValidCartItemId_ShouldDeleteCartItem() {
        Long cartItemId = 1L;

        User user = new User();
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);

        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, user.getId())).thenReturn(Optional.of(cartItem));
        shoppingCartService.delete(cartItemId, authentication);

        verify(authentication, times(1)).getPrincipal();
        verify(cartItemRepository, times(1)).findByIdAndShoppingCartId(cartItemId, user.getId());
        verify(cartItemRepository, times(1)).delete(cartItem);

        verifyNoMoreInteractions(cartItemRepository);
        verifyNoInteractions(shoppingCartMapper, bookRepository, shoppingCartRepository);
    }

    @Test
    @DisplayName("Check if exception was threw by non existing cart item")
    public void delete_InvalidCartItemId_ShouldThrowException() {
        User user = new User();
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        Long nonExistingId = 12L;

        when(cartItemRepository.findByIdAndShoppingCartId(nonExistingId, user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.delete(nonExistingId, authentication));

        String expected = "Can't find cart item with ids: cart item id "
                + nonExistingId + " shopping cart id " + user.getId();
        String actual = exception.getMessage();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(authentication, times(1)).getPrincipal();
        verify(cartItemRepository, times(1)).findByIdAndShoppingCartId(nonExistingId, user.getId());
        verifyNoMoreInteractions(cartItemRepository);
        verifyNoInteractions(bookRepository, shoppingCartMapper, shoppingCartRepository);
    }

    @Test
    @DisplayName("Check if exception was threw by invalid cart item id")
    public void update_InvalidCartItemId_ShouldThrowException() {
        Long invalidId = 12L;
        CartItemQuantityRequestDto cartItemQuantityRequestDto = new CartItemQuantityRequestDto(7);

        User user = new User();
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(cartItemRepository.findByIdAndShoppingCartId(invalidId, user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.update(invalidId, cartItemQuantityRequestDto, authentication));
        String expected = "Can't find cart item with ids: cart item id "
                + invalidId + " shopping cart id " + user.getId();
        String actual = exception.getMessage();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(authentication, times(1)).getPrincipal();
        verify(cartItemRepository, times(1)).findByIdAndShoppingCartId(invalidId, user.getId());

        verifyNoMoreInteractions(cartItemRepository);
        verifyNoInteractions(bookRepository, shoppingCartMapper, shoppingCartRepository);
    }

    @Test
    @DisplayName("Check if quantity was updated")
    public void update_ValidData_ShouldUpdateQuantity() {
        Long cartItemId = 12L;
        CartItemQuantityRequestDto cartItemQuantityRequestDto = new CartItemQuantityRequestDto(7);

        User user = new User();
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Book book = getBookModel();

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(new ShoppingCart());

        ShoppingCart shoppingCart = new ShoppingCart();

        CartItemDto cartItemDto = new CartItemDto(1L, book.getId(), book.getTitle(), cartItemQuantityRequestDto.quantity());

        Set<CartItemDto> cartItemDtos = new HashSet<>();
        cartItemDtos.add(cartItemDto);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(1L, user.getId(),cartItemDtos);

        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, user.getId())).thenReturn(Optional.of(cartItem));
        when(shoppingCartRepository.getShoppingCartByUserId(user.getId())).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actual = shoppingCartService.update(cartItemId, cartItemQuantityRequestDto, authentication);

        assertEquals(cartItemQuantityRequestDto.quantity(), cartItem.getQuantity());

        verify(authentication, times(1)).getPrincipal();
        verify(cartItemRepository, times(1)).findByIdAndShoppingCartId(cartItemId, user.getId());
        verify(shoppingCartRepository, times(1)).getShoppingCartByUserId(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);

        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository, shoppingCartMapper);
        verifyNoInteractions(bookRepository);
    }



    private static @NonNull Book getBookModel() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        Book bookModel = new Book();
        bookModel.setId(1L);
        bookModel.setTitle("Kobzar");
        bookModel.setAuthor("Taras Shevchenko");
        bookModel.setIsbn("23042343");
        bookModel.setPrice(BigDecimal.ONE);
        bookModel.setDescription("Book by Taras Shevchenko");
        bookModel.setCoverImage("Image");
        bookModel.setCategories(categories);
        bookModel.setDeleted(false);
        return bookModel;
    }
}
