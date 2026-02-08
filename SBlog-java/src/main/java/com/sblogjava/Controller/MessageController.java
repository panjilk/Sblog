package com.sblogjava.Controller;

import com.sblogjava.Dto.MessageDto;
import com.sblogjava.Dto.MessagePageResult;
import com.sblogjava.Dto.MessageReplyRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public Result<MessagePageResult> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(messageService.getMessages(page, pageSize, status));
    }

    @PostMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestBody MessageReplyRequest request) {
        try {
            messageService.replyMessage(id, request);
            return Result.success("回复成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
