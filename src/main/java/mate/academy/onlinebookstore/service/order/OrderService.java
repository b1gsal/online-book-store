package mate.academy.onlinebookstore.service.order;

import java.util.List;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderShoppingAddressDto;
import mate.academy.onlinebookstore.dto.order.OrderStatusRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderDto createOrder(OrderShoppingAddressDto shoppingAddressDto, Authentication authentication);

    Page<OrderDto> getOrders(Authentication authentication, Pageable pageable);

    List<OrderItemDto> getOrderItems(Long orderId, Authentication authentication);

    OrderItemDto getOrderItem(Long orderId, Long itemId, Authentication authentication);

    OrderDto updateStatus(Long id, OrderStatusRequestDto statusRequestDto);
}
