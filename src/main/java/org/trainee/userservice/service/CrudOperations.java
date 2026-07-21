package org.trainee.userservice.service;

import java.util.List;

public interface CrudOperations<ReqDto, ResDto> {
    ResDto create(ReqDto dto);
    ResDto update(Integer id, ReqDto dto);
    void delete(Integer id);
    ResDto getById(Integer id);
    List<ResDto> getAll();
}
