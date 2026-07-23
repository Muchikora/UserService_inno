package org.trainee.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trainee.userservice.dto.request.UserRequestDto;
import org.trainee.userservice.dto.response.UserResponseDto;
import org.trainee.userservice.exception.EmailAlreadyExistsException;
import org.trainee.userservice.exception.RecordNotFoundException;
import org.trainee.userservice.exception.RecordStillActiveException;
import org.trainee.userservice.mapper.UserMapper;
import org.trainee.userservice.repository.UserRepository;
import org.trainee.userservice.service.*;
import org.trainee.userservice.specification.UserSpecification;
import org.trainee.userservice.specification.filter.UserFilter;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService implements CrudOperations<UserRequestDto, UserResponseDto>,
        ActivationOperations,
        FilterOperations<UserResponseDto, UserFilter>
{
    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    public UserResponseDto create(UserRequestDto userDto) {
        if (repository.existsByEmail(userDto.getEmail()))
            throw new EmailAlreadyExistsException(userDto.getEmail());

        var user = mapper.map(userDto);
        user.setActive(true);
        var created = repository.save(user);
        return mapper.map(created);
    }

    @CachePut(value = "users", key = "#id")
    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userDto) {
        if (repository.existsByEmail(userDto.getEmail()))
            throw new EmailAlreadyExistsException(userDto.getEmail());

        var user = repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        mapper.updateEntityFromDto(userDto, user);
        return mapper.map(user);
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    @Transactional
    public void delete(Long id) {
        var user = repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        if (user.getActive())
            throw new RecordStillActiveException(id);

        repository.delete(user);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public UserResponseDto getById(Long id) {
        var user = repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        return mapper.map(user);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return mapper.map(repository.findAll());
    }

    @CachePut(value = "users", key = "#id")
    @Override
    @Transactional
    public void activate(Long id) {
        repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        repository.activate(id);
    }

    @CachePut(value = "users", key = "#id")
    @Override
    @Transactional
    public void deactivate(Long id) {
        repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        repository.deactivate(id);
    }

    @Override
    public Page<UserResponseDto> getFiltered(UserFilter filter, Pageable pageable) {
        var spec = Specification.where(UserSpecification.byFilter(filter));
        var page = repository.findAll(spec, pageable);
        return page.map(mapper::map);
    }

    public List<UserResponseDto> getAllCreatedAfter(LocalDateTime date) {
        return mapper.map(repository.findAllByCreatedAtAfter(date));
    }
}
