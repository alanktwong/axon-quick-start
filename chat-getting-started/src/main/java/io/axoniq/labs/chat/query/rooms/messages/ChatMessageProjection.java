package io.axoniq.labs.chat.query.rooms.messages;

import java.time.Instant;
import java.util.List;

import io.axoniq.labs.chat.coreapi.MessagePostedEvent;
import io.axoniq.labs.chat.coreapi.RoomMessagesQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class ChatMessageProjection
{
    private final ChatMessageRepository repository;

    private final QueryUpdateEmitter updateEmitter;

    public ChatMessageProjection(final ChatMessageRepository repository,
        final QueryUpdateEmitter updateEmitter)
    {
        this.repository = repository;
        this.updateEmitter = updateEmitter;
    }

    // TODO: Create some event handlers that update this model when necessary
    @EventHandler
    public void on(final MessagePostedEvent event, @Timestamp final Instant timestamp)
    {
        checkNotNull(event.getRoomId());
        checkNotNull(event.getParticipant());
        checkNotNull(event.getMessage());

        final var chatMessage = new ChatMessage(event.getParticipant(), event.getRoomId(), event.getMessage(), timestamp.toEpochMilli());
        repository.save(chatMessage);

        updateEmitter.emit(RoomMessagesQuery.class,
            query -> event.getRoomId().equals(query.getRoomId()),
            chatMessage);
    }

    // TODO: Create the query handler to read data from this model
    @QueryHandler
    public List<ChatMessage> handle(final RoomMessagesQuery query)
    {
        final var roomId = query.getRoomId();
        checkNotNull(roomId);

        return repository.findAllByRoomIdOrderByTimestamp(roomId);
    }

    // TODO: Emit updates when new message arrive to notify subscription query by modifying the event handler
}
