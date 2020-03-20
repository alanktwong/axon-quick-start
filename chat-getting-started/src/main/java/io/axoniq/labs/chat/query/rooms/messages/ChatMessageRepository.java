package io.axoniq.labs.chat.query.rooms.messages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>
{
    List<ChatMessage> findAllByRoomIdOrderByTimestamp(String roomId);
}
