package com.sblogjava.service;

import com.sblogjava.Dto.MessageDto;
import com.sblogjava.Dto.MessagePageResult;
import com.sblogjava.Dto.MessageReplyRequest;

public interface MessageService {
    MessagePageResult getMessages(Integer page, Integer pageSize, String status);

    void replyMessage(Long id, MessageReplyRequest request);

    void deleteMessage(Long id);
}
