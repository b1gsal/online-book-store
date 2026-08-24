package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.cartitem.CartItemQuantityRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add book", description = "Add book to user's shopping cart")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ShoppingCartDto addBooks(
            @RequestBody
            @Valid CartItemRequestDto cartItemRequestDto,
            Authentication authentication) {
        return shoppingCartService.addBooks(cartItemRequestDto, authentication);
    }

    @Operation(summary = "Get shopping cart",
            description = "Get info about user's shopping cart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication);
    }

    @Operation(summary = "Update shopping cart",
            description = "Update cart item in user's shopping cart by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody @Valid CartItemQuantityRequestDto cartItemQuantityRequestDto,
            Authentication authentication) {
        return shoppingCartService.update(cartItemId, cartItemQuantityRequestDto, authentication);
    }

    @Operation(summary = "Delete cart item",
            description = "Delete cart item by id in user's shopping cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/items/{cartItemId}")
    public void delete(@PathVariable Long cartItemId, Authentication authentication) {
        shoppingCartService.delete(cartItemId, authentication);
    }
}
