package org.trainee.userservice.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trainee.userservice.dto.request.PaymentCardRequestDto;
import org.trainee.userservice.dto.response.PaymentCardResponseDto;
import org.trainee.userservice.service.impl.PaymentCardService;
import org.trainee.userservice.specification.filter.PaymentCardFilter;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {
    @Autowired
    private PaymentCardService service;

    @PostMapping("/payment-card")
    public ResponseEntity<PaymentCardResponseDto> create(@Valid @RequestBody PaymentCardRequestDto cardDto) {
        var response = service.create(cardDto);
        return ResponseEntity.created(URI.create("/api/cards/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> update(@PathVariable Integer id,
                                                         @Valid @RequestBody PaymentCardRequestDto cardDto) {
        var response = service.update(id, cardDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<PaymentCardResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Integer id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<PaymentCardResponseDto>> getFiltered(@ParameterObject PaymentCardFilter filter,
                                                                    @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.getFiltered(filter, pageable));
    }

    @GetMapping("/connected/{userId}")
    public ResponseEntity<List<PaymentCardResponseDto>> getByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }
}
