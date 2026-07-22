package org.trainee.userservice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trainee.userservice.dto.request.PaymentCardRequestDto;
import org.trainee.userservice.dto.response.PaymentCardResponseDto;
import org.trainee.userservice.exception.RecordNotFoundException;
import org.trainee.userservice.exception.RecordStillActiveException;
import org.trainee.userservice.mapper.PaymentCardMapper;
import org.trainee.userservice.repository.PaymentCardRepository;
import org.trainee.userservice.repository.UserRepository;
import org.trainee.userservice.service.Activatable;
import org.trainee.userservice.service.CrudOperations;
import org.trainee.userservice.service.Filterable;
import org.trainee.userservice.specification.PaymentCardSpecification;
import org.trainee.userservice.specification.filter.PaymentCardFilter;

import java.util.List;

@Service
public class PaymentCardService implements CrudOperations<PaymentCardRequestDto, PaymentCardResponseDto>,
        Activatable,
        Filterable<PaymentCardResponseDto, PaymentCardFilter>
{
    private final PaymentCardMapper mapper;
    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;

    public PaymentCardService(PaymentCardMapper mapper,
                              PaymentCardRepository cardRepository,
                              UserRepository userRepository) {
        this.mapper = mapper;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PaymentCardResponseDto create(PaymentCardRequestDto cardDto) {
        var user = userRepository.findById(cardDto.getUserId()).orElseThrow(() -> new RecordNotFoundException(cardDto.getUserId()));
        var card = mapper.map(cardDto);
        card.setActive(true);
        card.setUser(user);
        var created = cardRepository.save(card);
        return mapper.map(created);
    }

    @Override
    @Transactional
    public PaymentCardResponseDto update(Integer id, PaymentCardRequestDto cardDto) {
        var card = cardRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        mapper.updateEntityFromDto(cardDto, card);
        return mapper.map(card);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        var card = cardRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        if (card.getActive())
            throw new RecordStillActiveException(id);

        cardRepository.delete(card);
    }

    @Override
    public PaymentCardResponseDto getById(Integer id) {
        var card = cardRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        return mapper.map(card);
    }

    @Override
    public List<PaymentCardResponseDto> getAll() {
        var cards = cardRepository.findAll();
        return mapper.map(cards);
    }

    @Override
    @Transactional
    public void activate(Integer id) {
        cardRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        cardRepository.activate(id);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        cardRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
        cardRepository.deactivate(id);
    }

    @Override
    public Page<PaymentCardResponseDto> getFiltered(PaymentCardFilter filter, Pageable pageable) {
        var spec = Specification.where(PaymentCardSpecification.byFilter(filter));
        var page = cardRepository.findAll(spec, pageable);
        return page.map(mapper::map);
    }

    public List<PaymentCardResponseDto> getByUserId(Integer userId) {
        var card = cardRepository.findByUserId(userId);
        return mapper.map(card);
    }
}
