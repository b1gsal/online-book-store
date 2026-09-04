package mate.academy.onlinebookstore.controller;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.onlinebookstore.config.AbstractTestContainers;
import mate.academy.onlinebookstore.dto.book.BookDto;
import mate.academy.onlinebookstore.dto.book.CreateBookRequestDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest extends AbstractTestContainers {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
            ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/category/insert-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create new book")
    public void createBook_ValidRequestDto_ShouldReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "The Bourne Identity",
                "Robert Ludlum",
                "978-0553270433",
                BigDecimal.valueOf(15.99),
                "History about memory loss",
                "https://example.com/images/bourne_cover.jpg",
                List.of(1L));

        BookDto expected = new BookDto(
                null,
                "The Bourne Identity",
                "Robert Ludlum",
                "978-0553270433",
                BigDecimal.valueOf(15.99),
                "History about memory loss",
                "https://example.com/images/bourne_cover.jpg",
                List.of(1L));



        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(contentAsString, BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.id());

        boolean checkIfActualEqualExpected = EqualsBuilder.reflectionEquals(expected, actual, "id");
        Assertions.assertTrue(checkIfActualEqualExpected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if thre books were return")
    public void getAll_ValidData_ShouldReturnPageOfThreeBooks() throws Exception {
        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].title").value("The Bourne Identity"))
                .andExpect(jsonPath("$.content[1].title").value("First Blood"))
                .andExpect(jsonPath("$.content[2].title").value("Treasure Island"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book dto was returned by id")
    public void getBookById_ValidId_ShouldReturnBookDtoById() throws Exception {
        BookDto expected = new BookDto(
                1L,
                "The Bourne Identity",
                "Robert Ludlum",
                "978-0553270433",
                BigDecimal.valueOf(15.99),
                "History about memory loss",
                "https://example.com/images/bourne_cover.jpg",
                List.of(1L));

        MvcResult result = mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(contentAsString, BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book dto was deleted by id")
    public void delete_ValidBookId_ShouldDeleteBookFromDb() throws Exception {
        mockMvc.perform(delete("/books/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        mockMvc.perform(get("/books/2"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if book was updated")
    public void update_ValidData_ShouldUpdateBook() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "The Bourne Identity Remake",
                "Robert Ludlum",
                "978-0553270433",
                BigDecimal.valueOf(20.99),
                "History about memory loss",
                "https://example.com/images/bourne_cover.jpg",
                List.of(1L));

        BookDto expected = new BookDto(
                1L,
                "The Bourne Identity Remake",
                "Robert Ludlum",
                "978-0553270433",
                BigDecimal.valueOf(20.99),
                "History about memory loss",
                "https://example.com/images/bourne_cover.jpg",
                List.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(contentAsString, BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Check if page of books wes returned by search parameters")
    public void search_ExistAuthor_ShouldReturnBooksByAuthor() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("authors", "Robert Ludlum")
                        .param("authors", "Robert Louis Stevenson")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("The Bourne Identity"))
                .andExpect(jsonPath("$.content[1].title").value("Treasure Island"));
    }
}
