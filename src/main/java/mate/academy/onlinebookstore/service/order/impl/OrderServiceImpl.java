package mate.academy.onlinebookstore.service.order.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.order.OrderDto;
import mate.academy.onlinebookstore.dto.order.OrderShoppingAddressDto;
import mate.academy.onlinebookstore.dto.order.OrderStatusRequestDto;
import mate.academy.onlinebookstore.dto.orderitem.OrderItemDto;
import mate.academy.onlinebookstore.exception.EmptyShoppingCartException;
import mate.academy.onlinebookstore.exception.OrderItemNotFoundException;
import mate.academy.onlinebookstore.exception.OrderNotFoundException;
import mate.academy.onlinebookstore.mapper.OrderItemMapper;
import mate.academy.onlinebookstore.mapper.OrderMapper;
import mate.academy.onlinebookstore.model.CartItem;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.OrderItem;
import mate.academy.onlinebookstore.model.ShoppingCart;
import mate.academy.onlinebookstore.model.Status;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.order.OrderRepository;
import mate.academy.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.onlinebookstore.service.order.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto createOrder(
            OrderShoppingAddressDto shippingAddressDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCartByUserId = shoppingCartRepository
                .getShoppingCartByUserId(user.getId());
        if (shoppingCartByUserId.getCartItems().isEmpty()) {
            throw new EmptyShoppingCartException("User's shopping cart is empty");
        }
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUser(shoppingCartByUserId.getUser());
        order.setShippingAddress(shippingAddressDto.shippingAddress());
        order.setStatus(Status.NEW);

        BigDecimal total = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCartByUserId.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());

            BigDecimal price = cartItem.getBook().getPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }

        order.setTotal(total);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        shoppingCartByUserId.getCartItems().clear();
        shoppingCartRepository.save(shoppingCartByUserId);

        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getOrders(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Order order = orderRepository.findByUserAndId(user, orderId).orElseThrow(
                () -> new OrderNotFoundException("Can't find order with id " + orderId));
        return order.getOrderItems().stream().map(orderItemMapper::toDto).toList();
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId,
                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Order order = orderRepository.findByUserAndId(user, orderId).orElseThrow(
                () -> new OrderNotFoundException("Can't find order with id " + orderId));
        OrderItem item = order.getOrderItems().stream()
                .filter(orderItem -> Objects.equals(orderItem.getId(), itemId))
                .findFirst()
                .orElseThrow(
                        () -> new OrderItemNotFoundException("Can't find item with id " + itemId));
        return orderItemMapper.toDto(item);
    }

    @Transactional
    @Override
    public OrderDto updateStatus(Long id,
                                 OrderStatusRequestDto statusRequestDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("Can't find order with id " + id));
        order.setStatus(statusRequestDto.status());
        return orderMapper.toDto(order);
    }
}
