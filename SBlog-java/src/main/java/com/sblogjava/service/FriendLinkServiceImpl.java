package com.sblogjava.service;

import com.sblogjava.Dto.FriendLinkDto;
import com.sblogjava.Dto.FriendLinkRequest;
import com.sblogjava.dao.FriendLink;
import com.sblogjava.dao.FriendLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkRepository friendLinkRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<FriendLinkDto> getAllLinks() {
        return friendLinkRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendLinkDto> getActiveLinks() {
        return friendLinkRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FriendLinkDto getById(Long id) {
        FriendLink link = friendLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("友情链接不存在"));
        return convertToDto(link);
    }

    @Override
    @Transactional
    public void create(FriendLinkRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("链接名称不能为空");
        }
        if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
            throw new RuntimeException("链接地址不能为空");
        }

        FriendLink link = new FriendLink();
        link.setName(request.getName());
        link.setUrl(request.getUrl());
        link.setLogo(request.getLogo());
        link.setDescription(request.getDescription());
        link.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        link.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        friendLinkRepository.save(link);
    }

    @Override
    @Transactional
    public void update(Long id, FriendLinkRequest request) {
        FriendLink link = friendLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("友情链接不存在"));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("链接名称不能为空");
        }
        if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
            throw new RuntimeException("链接地址不能为空");
        }

        link.setName(request.getName());
        link.setUrl(request.getUrl());
        link.setLogo(request.getLogo());
        link.setDescription(request.getDescription());
        link.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        link.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        friendLinkRepository.save(link);
    }

    @Override
    public void delete(Long id) {
        if (!friendLinkRepository.existsById(id)) {
            throw new RuntimeException("友情链接不存在");
        }
        friendLinkRepository.deleteById(id);
    }

    private FriendLinkDto convertToDto(FriendLink link) {
        FriendLinkDto dto = new FriendLinkDto();
        dto.setId(link.getId());
        dto.setName(link.getName());
        dto.setUrl(link.getUrl());
        dto.setLogo(link.getLogo());
        dto.setDescription(link.getDescription());
        dto.setSortOrder(link.getSortOrder());
        dto.setIsActive(link.getIsActive());
        dto.setCreatedAt(link.getCreatedAt() != null ?
                link.getCreatedAt().format(formatter) : null);
        return dto;
    }
}
