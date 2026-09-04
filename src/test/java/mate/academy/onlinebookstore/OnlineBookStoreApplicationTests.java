package mate.academy.onlinebookstore;

import mate.academy.onlinebookstore.config.AbstractTestContainers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OnlineBookStoreApplicationTests extends AbstractTestContainers {

    @Test
    void contextLoads() {
    }

}
