package org.trainee.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trainee.userservice.dto.request.UserRequestDto;
import org.trainee.userservice.model.User;
import org.trainee.userservice.repository.UserRepository;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTests extends BaseIntegrationTest {
    private final MockMvc mockMvc;
    private final UserRepository repository;

    UserControllerTests(MockMvc mockMvc,
                                      UserRepository repository) {
        this.mockMvc = mockMvc;
        this.repository = repository;
    }

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void create_shouldSave_user() throws Exception {
        var request = new UserRequestDto();
        request.setName("John");
        request.setSurname("Smith");
        request.setEmail("john@test.com");
        request.setBirthDate(LocalDate.of(1995, Month.JANUARY, 1));

        mockMvc.perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""" 
                        {
                            "name": "John",
                            "surname": "Smith",
                            "birthDate": "1995-01-01",
                            "email": "john@test.com"
                        }
                    """)
                ).andExpect(status().isCreated());

        var users = repository.findAll();
        assert users.size() == 1;
        var user = users.getFirst();

        assert user.getName().equals("John");
        assert user.getEmail().equals("john@test.com");
    }

    @Test
    void getById_shouldReturn_user() throws Exception {
        var user = createUser();

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void getAll_shouldReturn_users() throws Exception {
        createUser();
        var second = new User();
        second.setName("Alex");
        second.setSurname("Brown");
        second.setBirthDate(LocalDate.of(2000, Month.JANUARY, 1));
        second.setEmail("alex@test.com");
        second.setActive(true);
        repository.save(second);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void update_shouldChange_user() throws Exception {
        var user = createUser();

        mockMvc.perform(
                put("/api/users/" + user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "Updated",
                            "surname": "User",
                            "birthDate": "1990-01-01",
                            "email": "updated@test.com"
                        }
                    """)
                ).andExpect(status().isOk());

        var updated = repository.findById(user.getId()).orElseThrow();

        assertEquals("Updated", updated.getName());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test
    void delete_shouldntRemove_activeUser() throws Exception {
        var user = createUser();

        mockMvc.perform(delete("/api/users/" + user.getId())).andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldRemove_inactiveUser() throws Exception {
        var user = createInactiveUser();

        mockMvc.perform(delete("/api/users/" + user.getId())).andExpect(status().isNoContent());

        assertFalse(repository.existsById(user.getId()));
    }

    @Test
    void getById_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999")).andExpect(status().isNotFound());
    }

    private User createUser() {
        var user = new User();
        user.setName("John");
        user.setSurname("Smith");
        user.setBirthDate(LocalDate.of(1995, Month.JANUARY, 1));
        user.setEmail("john@test.com");
        user.setActive(true);

        return repository.save(user);
    }

    private User createInactiveUser() {
        var user = createUser();
        user.setActive(false);

        return repository.save(user);
    }
}
