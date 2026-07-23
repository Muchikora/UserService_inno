package org.trainee.userservice.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trainee.userservice.dto.request.UserRequestDto;
import org.trainee.userservice.dto.response.UserResponseDto;
import org.trainee.userservice.service.impl.UserService;
import org.trainee.userservice.specification.filter.UserFilter;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userDto) {
        var response = service.create(userDto);
        return ResponseEntity.created(URI.create("/api/users/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody UserRequestDto userDto) {
        var response = service.update(id, userDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<UserResponseDto>> getFiltered(@ParameterObject UserFilter filter,
                                                             @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.getFiltered(filter, pageable));
    }

    @GetMapping("/createdAfter")
    public ResponseEntity<List<UserResponseDto>> getAllCreatedAfter(@RequestParam LocalDateTime date) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllCreatedAfter(date));
    }
}
