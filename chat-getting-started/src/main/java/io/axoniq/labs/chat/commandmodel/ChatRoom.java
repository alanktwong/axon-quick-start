package io.axoniq.labs.chat.commandmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.axoniq.labs.chat.coreapi.CreateRoomCommand;
import io.axoniq.labs.chat.coreapi.JoinRoomCommand;
import io.axoniq.labs.chat.coreapi.LeaveRoomCommand;
import io.axoniq.labs.chat.coreapi.MessagePostedEvent;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.PostMessageCommand;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Aggregate
public class ChatRoom
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @AggregateIdentifier
    private String roomId;

    private Map<String, List<String>> selectedParticipants;

    public ChatRoom()
    {
        // Required by Axon
    }

    @CommandHandler
    public ChatRoom(final CreateRoomCommand command)
    {
        logger.info("Creating the chat room: {} named: {}", command.getRoomId(), command.getName());
        AggregateLifecycle.apply(new RoomCreatedEvent(command.getRoomId(), command.getName()));
    }

    @EventSourcingHandler
    public void on(final RoomCreatedEvent event)
    {
        roomId = event.getRoomId();
        selectedParticipants = new HashMap<>();
        logger.info("created the chat room: {} named {}", roomId, event.getName());
    }

    @CommandHandler
    public void handle(final JoinRoomCommand command)
    {
        final var participant = command.getParticipant();
        final var roomId = command.getRoomId();
        if (!selectedParticipants.containsKey(participant))
        {
            logger.info("{} joining the chat room: {}", participant, roomId);
            AggregateLifecycle.apply(new ParticipantJoinedRoomEvent(participant, roomId));
        }
    }

    @EventSourcingHandler
    public void on(final ParticipantJoinedRoomEvent event)
    {
        final var participant = event.getParticipant();
        checkState(!selectedParticipants.containsKey(participant));
        selectedParticipants.put(participant, new ArrayList<>());
        logger.info("{} joined the chat room: {}", participant, event.getRoomId());
    }

    @CommandHandler
    public void handle(final PostMessageCommand command)
    {
        final var participant = command.getParticipant();
        checkState(selectedParticipants.containsKey(participant));
        final var roomId = command.getRoomId();
        final var message = command.getMessage();
        logger.info("{} posting the message [{}] in chat room: {}", participant, message, roomId);
        AggregateLifecycle.apply(new MessagePostedEvent(participant, roomId, message));
    }

    @EventSourcingHandler
    public void on(final MessagePostedEvent event)
    {
        final var participant = event.getParticipant();
        final var message = event.getMessage();

        checkNotNull(participant);
        checkState(selectedParticipants.containsKey(participant));
        selectedParticipants.merge(participant, selectedParticipants.get(participant), (old, newMessages) -> {
            old.addAll(List.of(message));
            return old;
        });
        logger.info("{} posted the message [{}] in the chat room: {}", participant, message, event.getRoomId());
    }

    @CommandHandler
    public void handle(final LeaveRoomCommand command)
    {
        final var participant = command.getParticipant();
        if (selectedParticipants.containsKey(participant))
        {
            final var roomId = command.getRoomId();
            logger.info("{} leaving the chat room: {}", participant, roomId);
            AggregateLifecycle.apply(new ParticipantLeftRoomEvent(participant, roomId));
        }
    }

    @EventSourcingHandler
    public void on(final ParticipantLeftRoomEvent event)
    {
        final var participant = event.getParticipant();
        checkNotNull(participant);
        checkState(selectedParticipants.containsKey(participant));
        selectedParticipants.remove(participant);
        logger.info("{} left the chat room: {}", participant, event.getRoomId());
    }
}
