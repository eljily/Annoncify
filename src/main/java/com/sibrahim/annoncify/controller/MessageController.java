package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.entity.Message;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.repository.MessageRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{sender}/{recipient}")
    public List<Message> getMessages(@PathVariable User sender, @PathVariable User recipient) {
        return messageRepository.findBySenderOrRecipientOrderByTimestamp(sender, recipient);
    }

    @PostMapping
    public void sendMessage(@RequestBody Message message) {
        // Save the message to the database
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }
}
