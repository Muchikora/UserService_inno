package org.trainee.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trainee.userservice.model.PaymentCard;
import org.trainee.userservice.model.User;
import org.trainee.userservice.repository.PaymentCardRepository;
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

class PaymentCardControllerTests extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentCardRepository cardRepository;

    @BeforeEach
    void cleanDatabase() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void create_shouldSave_card() throws Exception {
        var user = createUser();

        mockMvc.perform(
                post("/api/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "number": "1234567890123456",
                            "holder": "JOHN SMITH",
                            "expirationDate": "2030-01-01",
                            "userId": %d
                        }
                    """.formatted(user.getId()))
                ).andExpect(status().isCreated());

        var cards = cardRepository.findAll();

        assertEquals(1, cards.size());
        assertEquals("1234567890123456", cards.getFirst().getNumber());
        assertEquals(user.getId(), cards.getFirst().getUser().getId());
    }

    @Test
    void getById_shouldReturn_card() throws Exception {
        var user = createUser();
        var card = createInactiveCard(user);

        mockMvc.perform(get("/api/cards/" + card.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("1234567890123456"))
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    void getByUserId_shouldReturn_cards() throws Exception {
        var user = createUser();
        createInactiveCard(user);
        createInactiveCard(user);

        mockMvc.perform(get("/api/cards/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void update_shouldChange_Card() throws Exception {
        var user = createUser();
        var card = createInactiveCard(user);

        mockMvc.perform(
                put("/api/cards/" + card.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "number": "9999999999999999",
                            "holder": "UPDATED USER",
                            "expirationDate": "2035-01-01",
                            "userId": %d
                        }
                    """.formatted(user.getId()))
                ).andExpect(status().isOk());

        var updated = cardRepository.findById(card.getId()).orElseThrow();

        assertEquals("9999999999999999", updated.getNumber());
    }

    @Test
    void delete_shouldRemove_card() throws Exception {
        var user = createUser();
        var card = createInactiveCard(user);

        mockMvc.perform(delete("/api/cards/" + card.getId())).andExpect(status().isNoContent());

        assertFalse(cardRepository.existsById(card.getId()));
    }

    @Test
    void create_shouldFail_whenUserNotFound() throws Exception {
        mockMvc.perform(
                post("/api/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "number": "1234567890123456",
                            "holder": "JOHN SMITH",
                            "expirationDate": "2030-01-01",
                            "userId": 999
                        }
                    """)
                ).andExpect(status().isNotFound());
    }

    @Test
    void getByUserId_shouldReturnEmptyList_whenUserHasNoCards() throws Exception {
        var user = createUser();

        userRepository.save(user);

        mockMvc.perform(get("/api/cards/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void delete_shouldReturn400_whenCardIsActive() throws Exception {
        var user = createUser();
        var card = createActiveCard(user);

        cardRepository.save(card);

        mockMvc.perform(delete("/api/cards/{id}", card.getId()))
                .andExpect(status().isBadRequest());
    }

    private User createUser() {
        var user = new User();
        user.setName("John");
        user.setSurname("Smith");
        user.setBirthDate(LocalDate.of(1995, Month.JANUARY, 1));
        user.setEmail("john@test.com");
        user.setActive(true);

        return userRepository.save(user);
    }

    private PaymentCard createInactiveCard(User user) {
        return cardRepository.save(createCard(user));
    }

    private PaymentCard createActiveCard(User user) {
        var card = createCard(user);
        card.setActive(true);
        return cardRepository.save(card);
    }

    private PaymentCard createCard(User user) {
        var card = new PaymentCard();
        card.setNumber("1234567890123456");
        card.setHolder("JOHN SMITH");
        card.setExpirationDate(LocalDate.of(2030, Month.JANUARY, 1));
        card.setActive(false);
        card.setUser(user);

        return card;
    }
}
