package org.trainee.userservice.service;

import java.util.List;

public interface CrudOperations<Q, S> {
    S create(Q dto);
    S update(Long id, Q dto);
    void delete(Long id);
    S getById(Long id);
    List<S> getAll();
}
