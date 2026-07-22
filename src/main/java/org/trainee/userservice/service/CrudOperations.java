package org.trainee.userservice.service;

import java.util.List;

public interface CrudOperations<Q, S> {
    S create(Q dto);
    S update(Integer id, Q dto);
    void delete(Integer id);
    S getById(Integer id);
    List<S> getAll();
}
