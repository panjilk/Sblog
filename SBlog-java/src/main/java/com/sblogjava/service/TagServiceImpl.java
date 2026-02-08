package com.sblogjava.service;

import com.sblogjava.Dto.TagDto;
import com.sblogjava.Dto.TagRequest;
import com.sblogjava.dao.Tag;
import com.sblogjava.dao.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<TagDto> findAll() {
        return tagRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TagDto findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        return convertToDto(tag);
    }

    @Override
    public void create(TagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new RuntimeException("标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        tagRepository.save(tag);
    }

    @Override
    public void update(Long id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));

        if (!tag.getName().equals(request.getName()) &&
                tagRepository.existsByName(request.getName())) {
            throw new RuntimeException("标签名称已存在");
        }

        tag.setName(request.getName());
        tagRepository.save(tag);
    }

    @Override
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("标签不存在");
        }
        tagRepository.deleteById(id);
    }

    private TagDto convertToDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setArticleCount(tag.getArticles() != null ? tag.getArticles().size() : 0);
        dto.setCreatedAt(tag.getCreatedAt() != null ?
                tag.getCreatedAt().format(formatter) : null);
        return dto;
    }
}
