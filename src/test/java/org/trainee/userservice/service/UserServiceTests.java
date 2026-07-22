package org.trainee.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trainee.userservice.dto.request.UserRequestDto;
import org.trainee.userservice.dto.response.UserResponseDto;
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
public class UserServiceTests {
    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void create_shouldCreateUser() {
        var dto = new UserRequestDto();
        var entity = new User();
        var saved = new User();
        saved.setId(1);

        var response = new UserResponseDto();
        response.setId(1);

        when(mapper.map(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.map(saved)).thenReturn(response);

        var result = service.create(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertTrue(entity.getActive());

        verify(mapper).map(dto);
        verify(repository).save(entity);
        verify(mapper).map(saved);
    }

    @Test
    void update_shouldUpdateUser() {
        var dto = new UserRequestDto();
        var entity = new User();
        var response = new UserResponseDto();

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.map(entity)).thenReturn(response);

        var result = service.update(1, dto);

        assertEquals(response, result);

        verify(mapper).updateEntityFromDto(dto, entity);
    }

    @Test
    void delete_shouldDeleteUser() {
        var user = new User();
        user.setActive(false);

        when(repository.findById(1)).thenReturn(Optional.of(user));
        service.delete(1);

        verify(repository).delete(user);
    }

    @Test
    void delete_shouldThrow_ifUserIsActive() {
        var user = new User();
        user.setActive(true);

        when(repository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(RecordStillActiveException.class, () -> service.delete(1));

        verify(repository, never()).delete((User) any());
    }

    @Test
    void getById_shouldReturnUser() {
        var entity = new User();
        var dto = new UserResponseDto();

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.map(entity)).thenReturn(dto);

        var result = service.getById(1);

        assertEquals(dto, result);

        verify(repository).findById(1);
        verify(mapper).map(entity);
    }

    @Test
    void activate_shouldCallRepository() {
        when(repository.findById(1)).thenReturn(Optional.of(new User()));

        service.activate(1);

        verify(repository).activate(1);
    }

    @Test
    void activate_shouldThrow_ifUserNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.activate(1));

        verify(repository, never()).activate(anyInt());
    }

    @Test
    void deactivate_shouldCallRepository() {
        when(repository.findById(1)).thenReturn(Optional.of(new User()));

        service.deactivate(1);

        verify(repository).deactivate(1);
    }

    @Test
    void deactivate_shouldThrow_ifUserNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.deactivate(1));

        verify(repository, never()).deactivate(anyInt());
    }

    @Test
    void getAll_shouldReturnMappedUsers() {
        var users = List.of(new User());
        var dto = List.of(new UserResponseDto());

        when(repository.findAll()).thenReturn(users);

        when(mapper.map(users)).thenReturn(dto);

        assertEquals(dto, service.getAll());
    }
}
