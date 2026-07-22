package org.trainee.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trainee.userservice.dto.request.PaymentCardRequestDto;
import org.trainee.userservice.dto.response.PaymentCardResponseDto;
import org.trainee.userservice.exception.RecordNotFoundException;
import org.trainee.userservice.exception.RecordStillActiveException;
import org.trainee.userservice.mapper.PaymentCardMapper;
import org.trainee.userservice.model.PaymentCard;
import org.trainee.userservice.model.User;
import org.trainee.userservice.repository.PaymentCardRepository;
import org.trainee.userservice.repository.UserRepository;
import org.trainee.userservice.service.impl.PaymentCardService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTests {
    @Mock
    private PaymentCardRepository cardRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper mapper;

    @InjectMocks
    private PaymentCardService service;

    @Test
    void create_shouldCreate_card() {
        var dto = new PaymentCardRequestDto();
        dto.setUserId(1);
        var user = new User();
        var entity = new PaymentCard();
        var saved = new PaymentCard();
        saved.setId(10);
        var response = new PaymentCardResponseDto();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(mapper.map(dto)).thenReturn(entity);
        when(cardRepository.save(entity)).thenReturn(saved);
        when(mapper.map(saved)).thenReturn(response);

        var result = service.create(dto);

        assertEquals(response, result);
        assertTrue(entity.getActive());
        assertEquals(user, entity.getUser());

        verify(cardRepository).save(entity);
    }

    @Test
    void update_shouldUpdate_card() {
        var dto = new PaymentCardRequestDto();
        var entity = new PaymentCard();
        var response = new PaymentCardResponseDto();

        when(cardRepository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.map(entity)).thenReturn(response);

        var result = service.update(1, dto);

        assertEquals(response, result);

        verify(mapper).updateEntityFromDto(dto, entity);
        verify(mapper).map(entity);
    }

    @Test
    void update_shouldThrow_ifCardNotFound() {
        when(cardRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.update(1, new PaymentCardRequestDto()));

        verify(mapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void delete_shouldDelete_inactiveCard() {
        var card = new PaymentCard();
        card.setActive(false);

        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        service.delete(1);

        verify(cardRepository).delete(card);
    }

    @Test
    void delete_shouldThrow_ifCardIsActive() {
        var card = new PaymentCard();
        card.setActive(true);

        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        assertThrows(RecordStillActiveException.class, () -> service.delete(1));

        verify(cardRepository, never()).delete((PaymentCard) any());
    }

    @Test
    void delete_shouldThrow_ifCardNotFound() {
        when(cardRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.delete(1));

        verify(cardRepository, never()).delete((PaymentCard) any());
    }

    @Test
    void create_shouldThrow_ifUserNotFound() {
        var dto = new PaymentCardRequestDto();
        dto.setUserId(5);

        when(userRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.create(dto));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void getByUserId_shouldReturn_cards() {
        var cards = List.of(new PaymentCard());
        var dto = List.of(new PaymentCardResponseDto());

        when(cardRepository.findByUserId(1)).thenReturn(cards);
        when(mapper.map(cards)).thenReturn(dto);

        var result = service.getByUserId(1);

        assertEquals(dto, result);

        verify(cardRepository).findByUserId(1);
        verify(mapper).map(cards);
    }
}
