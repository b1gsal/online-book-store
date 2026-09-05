package mate.academy.onlinebookstore.controller;

import mate.academy.onlinebookstore.config.AbstractTestContainers;
import mate.academy.onlinebookstore.dto.category.CategoryDto;
import mate.academy.onlinebookstore.dto.category.CategoryRequestDto;
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
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest extends AbstractTestContainers {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new category")
    public void createCategory_ValidDto_ShouldSaveAndReturnCategoryDto() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("Action", "Movie action");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        CategoryDto expected = new CategoryDto(null, "Action", "Movie action");

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(contentAsString, CategoryDto.class);

        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/category/insert-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/clean-category-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all categories")
    public void getAll_ValidData_ShouldReturnPageOfTwoCategoryDtos() throws Exception {
        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Action"))
                .andExpect(jsonPath("$.content[1].name").value("Adventure"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/category/insert-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/clean-category-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get category by id")
    public void getCategoryById_ValidId_ShouldReturnCategoryDtoWithGivenId() throws Exception {
        CategoryDto expected = new CategoryDto(2L, "Adventure", "Movie adventure");

        MvcResult result = mockMvc.perform(get("/categories/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(contentAsString, CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/category/insert-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/clean-category-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update category by id")
    public void updateCategory_ValidData_ShouldUpdateAndReturnCategory() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                "Magic adventure",
                "Movie magic adventure");

        CategoryDto expected = new CategoryDto(
                2L,
                "Magic adventure",
                "Movie magic adventure");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/categories/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(contentAsString, CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/category/insert-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/clean-category-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete category by id")
    public void deleteCategory_ValidId_ShouldDeleteCategoryById() throws Exception {
        mockMvc.perform(delete("/categories/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/insert-3-books-into-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/clean-all-database.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get books by categry")
    public void getBooksByCategoryId_ValidCategoryId_ShouldReturnTwoBooks() throws Exception {
        mockMvc.perform(get("/categories/1/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("The Bourne Identity"))
                .andExpect(jsonPath("$.content[1].title").value("First Blood"));
    }
}
