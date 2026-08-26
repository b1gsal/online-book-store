package mate.academy.onlinebookstore.repository.order;

import java.util.Optional;
import mate.academy.onlinebookstore.model.Order;
import mate.academy.onlinebookstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);

    Optional<Order> findByUserAndId(User user, Long id);
}
