package mate.academy.onlinebookstore.controller;


import java.util.HashSet;
import java.util.Set;
import mate.academy.onlinebookstore.config.AbstractTestContainers;
import mate.academy.onlinebookstore.dto.cartitem.CartItemDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemQuantityRequestDto;
import mate.academy.onlinebookstore.dto.cartitem.CartItemRequestDto;
import mate.academy.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest extends AbstractTestContainers {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql",
            "classpath:database/shoppingcart/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcart/clean-shopping-cart.sql",
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book's quantity were updated after added to shopping cart")
    @WithUserDetails(value = "den@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
    public void addBooks_ValidData_ShouldUpdateCartItemInShoppingCart() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 3);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Set<CartItemDto> cartItemDtos = new HashSet<>();
        CartItemDto cartItemDto = new CartItemDto(1L, 1L, "The Bourne Identity", 7);
        cartItemDtos.add(cartItemDto);
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, cartItemDtos);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        ShoppingCartDto actual = objectMapper.readValue(response, ShoppingCartDto.class);

        assertNotNull(actual);
        assertEquals(2, actual.id());
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql",
            "classpath:database/shoppingcart/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcart/clean-shopping-cart.sql",
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if new book was added to shopping cart")
    @WithUserDetails(value = "den@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
    public void addBooks_NewBook_ShouldAddNewBookInShoppingCart() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(2L, 1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        Set<CartItemDto> cartItemDtos = new HashSet<>();
        CartItemDto cartItemDtoFirst = new CartItemDto(1L, 1L, "The Bourne Identity", 4);
        cartItemDtos.add(cartItemDtoFirst);
        CartItemDto cartItemDtoSecond = new CartItemDto(2L, 2L, "First Blood",1);
        cartItemDtos.add(cartItemDtoSecond);
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, cartItemDtos);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ShoppingCartDto actual = objectMapper.readValue(contentAsString, ShoppingCartDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql",
            "classpath:database/shoppingcart/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcart/clean-shopping-cart.sql",
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book's quantity were updated after added to shopping cart")
    @WithUserDetails(value = "den@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
    public void getShoppingCart_ValidData_ShouldReturnShoppingCart() throws Exception {
        Set<CartItemDto> cartItemDtos = new HashSet<>();
        CartItemDto cartItemDto = new CartItemDto(1L, 1L, "The Bourne Identity", 4);
        cartItemDtos.add(cartItemDto);
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, cartItemDtos);

        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ShoppingCartDto actual = objectMapper.readValue(contentAsString, ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql",
            "classpath:database/shoppingcart/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcart/clean-shopping-cart.sql",
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book's quantity were updated after added to shopping cart")
    @WithUserDetails(value = "den@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
    public void updateQuantity_ValidData_ShouldUpdateQuantity() throws Exception {
        CartItemQuantityRequestDto requestDto = new CartItemQuantityRequestDto(12);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        Set<CartItemDto> cartItemDtos = new HashSet<>();
        CartItemDto cartItemDto = new CartItemDto(1L, 1L, "The Bourne Identity", 12);
        cartItemDtos.add(cartItemDto);
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, cartItemDtos);

        MvcResult result = mockMvc.perform(put("/cart/items/1")
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ShoppingCartDto actual = objectMapper.readValue(contentAsString, ShoppingCartDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql",
            "classpath:database/shoppingcart/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcart/clean-shopping-cart.sql",
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book's quantity were updated after added to shopping cart")
    @WithUserDetails(value = "den@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
    public void delete_ValidId_ShouldDeleteCartItem() throws Exception {
        mockMvc.perform(delete("/cart/items/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        ShoppingCartDto actual = objectMapper.readValue(contentAsString, ShoppingCartDto.class);
        assertTrue(actual.cartItems().isEmpty());
    }
}
