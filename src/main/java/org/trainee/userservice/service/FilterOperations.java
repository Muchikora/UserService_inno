package org.trainee.userservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterOperations<S, F> {
    Page<S> getFiltered(F filter, Pageable pageable);
}
