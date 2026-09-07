package mate.academy.onlinebookstore.repository.shoppingcart;

import mate.academy.onlinebookstore.config.AbstractTestContainers;
import mate.academy.onlinebookstore.model.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest extends AbstractTestContainers {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql",
            "classpath:database/shoppingcart/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Check if shoppingCart was returned by userId")
    public void getShoppingCartByUserId_ValidUserId_ShouldReturnShoppingCart() {
        Long userId = 2L;

        ShoppingCart shoppingCartByUserId = shoppingCartRepository.getShoppingCartByUserId(userId);
        assertEquals(2, shoppingCartByUserId.getId());
        assertEquals("Denis", shoppingCartByUserId.getUser().getFirstName());
        assertEquals(1, shoppingCartByUserId.getCartItems().size());
    }
}
