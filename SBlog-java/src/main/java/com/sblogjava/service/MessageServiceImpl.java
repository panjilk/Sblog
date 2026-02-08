package com.sblogjava.service;

import com.sblogjava.Dto.MessageDto;
import com.sblogjava.Dto.MessagePageResult;
import com.sblogjava.Dto.MessageReplyRequest;
import com.sblogjava.dao.Message;
import com.sblogjava.dao.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public MessagePageResult getMessages(Integer page, Integer pageSize, String status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Message> messagePage;
        Message.MessageStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            statusEnum = Message.MessageStatus.valueOf(status.toUpperCase());
        }

        if (statusEnum == null) {
            messagePage = messageRepository.findAll(pageable);
        } else {
            messagePage = messageRepository.findByStatus(statusEnum, pageable);
        }

        MessagePageResult result = new MessagePageResult();
        result.setTotal(messagePage.getTotalElements());
        result.setList(messagePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public void replyMessage(Long id, MessageReplyRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        message.setReply(request.getReply());
        message.setStatus(Message.MessageStatus.REPLIED);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new RuntimeException("留言不存在");
        }
        messageRepository.deleteById(id);
    }

    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setNickname(message.getNickname());
        dto.setContact(message.getContact());
        dto.setStatus(message.getStatus().name().toLowerCase());
        dto.setReply(message.getReply());
        dto.setCreatedAt(message.getCreatedAt() != null ?
                message.getCreatedAt().format(formatter) : null);
        return dto;
    }
}
