package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Message;
import com.sibrahim.annoncify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrRecipientOrderByTimestamp(User sender, User recipient);
}
