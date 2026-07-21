package org.trainee.userservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Filterable<ResDto, Filter> {
    Page<ResDto> getFiltered(Filter filter, Pageable pageable);
}
