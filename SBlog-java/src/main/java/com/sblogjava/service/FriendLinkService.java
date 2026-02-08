package com.sblogjava.service;

import com.sblogjava.Dto.FriendLinkDto;
import com.sblogjava.Dto.FriendLinkRequest;

import java.util.List;

public interface FriendLinkService {
    List<FriendLinkDto> getAllLinks();

    List<FriendLinkDto> getActiveLinks();

    FriendLinkDto getById(Long id);

    void create(FriendLinkRequest request);

    void update(Long id, FriendLinkRequest request);

    void delete(Long id);
}
