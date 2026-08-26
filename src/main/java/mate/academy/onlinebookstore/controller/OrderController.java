package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderShoppingAddressDto;
import mate.academy.onlinebookstore.dto.order.OrderStatusRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.service.order.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order and order's item management",
        description = "Endpoints for managing orders and order's items")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Create order",
            description = "Create new order from shopping cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public OrderDto createOrder(
            @RequestBody @Valid OrderShoppingAddressDto shoppingAddressDto,
            Authentication authentication) {
        return orderService.createOrder(shoppingAddressDto, authentication);
    }

    @Operation(summary = "Get all orders",
            description = "Get page of all available user's orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public Page<OrderDto> getOrders(Authentication authentication,
                                    Pageable pageable) {
        return orderService.getOrders(authentication, pageable);
    }

    @Operation(summary = "Get order's items by order id",
            description = "Get list of all available items in order")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getOrderItems(@PathVariable Long orderId,
                                            Authentication authentication) {
        return orderService.getOrderItems(orderId, authentication);
    }

    @Operation(summary = "Get order's item by id",
            description = "Get available order's item by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication) {
        return orderService.getOrderItem(orderId, itemId, authentication);
    }

    @Operation(summary = "Update order's status",
            description = "Update order's status by id")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid OrderStatusRequestDto statusRequestDto) {
        return orderService.updateStatus(id, statusRequestDto);
    }
}
