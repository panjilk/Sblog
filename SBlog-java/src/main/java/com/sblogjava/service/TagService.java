package com.sblogjava.service;

import com.sblogjava.Dto.TagDto;
import com.sblogjava.Dto.TagRequest;

import java.util.List;

public interface TagService {
    List<TagDto> findAll();

    TagDto findById(Long id);

    void create(TagRequest request);

    void update(Long id, TagRequest request);

    void delete(Long id);
}
