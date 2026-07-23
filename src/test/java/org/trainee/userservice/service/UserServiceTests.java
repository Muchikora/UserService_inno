package org.trainee.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trainee.userservice.dto.request.UserRequestDto;
import org.trainee.userservice.dto.response.UserResponseDto;
import org.trainee.userservice.exception.EmailAlreadyExistsException;
import org.trainee.userservice.exception.RecordNotFoundException;
import org.trainee.userservice.exception.RecordStillActiveException;
import org.trainee.userservice.mapper.UserMapper;
import org.trainee.userservice.model.User;
import org.trainee.userservice.repository.UserRepository;
import org.trainee.userservice.service.impl.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void create_shouldCreate_user() {
        var dto = new UserRequestDto();
        var entity = new User();
        var saved = new User();
        saved.setId(1L);

        var response = new UserResponseDto();
        response.setId(1L);

        when(mapper.map(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.map(saved)).thenReturn(response);

        var result = service.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(entity.getActive());

        verify(mapper).map(dto);
        verify(repository).save(entity);
        verify(mapper).map(saved);
    }

    @Test
    void update_shouldUpdate_user() {
        var dto = new UserRequestDto();
        var entity = new User();
        var response = new UserResponseDto();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.map(entity)).thenReturn(response);

        var result = service.update(1L, dto);

        assertEquals(response, result);

        verify(mapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void delete_shouldDelete_user() {
        var user = new User();
        user.setActive(false);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        service.delete(1L);

        verify(repository).delete(user);
    }

    @Test
    void delete_shouldThrow_ifUserIsActive() {
        var user = new User();
        user.setActive(true);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RecordStillActiveException.class, () -> service.delete(1L));

        verify(repository, never()).delete((User) any());
    }

    @Test
    void getById_shouldReturn_user() {
        var entity = new User();
        var dto = new UserResponseDto();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.map(entity)).thenReturn(dto);

        var result = service.getById(1L);

        assertEquals(dto, result);

        verify(repository).findById(1L);
        verify(mapper).map(entity);
    }

    @Test
    void activate_shouldThrow_ifUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.activate(1L));

        verify(repository, never()).activate(anyLong());
    }

    @Test
    void deactivate_shouldThrow_ifUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.deactivate(1L));

        verify(repository, never()).deactivate(anyLong());
    }

    @Test
    void getAll_shouldReturn_mappedUsers() {
        var users = List.of(new User());
        var dto = List.of(new UserResponseDto());

        when(repository.findAll()).thenReturn(users);

        when(mapper.map(users)).thenReturn(dto);

        assertEquals(dto, service.getAll());
    }

    @Test
    void create_shouldThrow_ifEmailAlreadyExists() {
        var dto = new UserRequestDto();
        dto.setEmail("test@test.com");

        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,() -> service.create(dto));

        verify(repository, never()).save(any());
        verify(mapper, never()).map((User) any());
    }
}
