package io.axoniq.labs.chat.commandmodel;

import java.util.HashSet;
import java.util.Set;

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
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ChatRoom
{
    @AggregateIdentifier
    private String roomId;

    private Set<String> participants;

    public ChatRoom()
    {
    }

    @CommandHandler
    public ChatRoom(final CreateRoomCommand command)
    {
        apply(new RoomCreatedEvent(command.getRoomId(), command.getName()));
    }

    @CommandHandler
    public void handle(final JoinRoomCommand command)
    {
        if (!participants.contains(command.getParticipant()))
        {
            apply(new ParticipantJoinedRoomEvent(command.getParticipant(), roomId));
        }
    }

    @CommandHandler
    public void handle(final LeaveRoomCommand command)
    {
        if (participants.contains(command.getParticipant()))
        {
            apply(new ParticipantLeftRoomEvent(command.getParticipant(), roomId));
        }
    }

    @CommandHandler
    public void handle(final PostMessageCommand command)
    {
        Assert.state(participants.contains(command.getParticipant()),
            "You cannot post messages unless you've joined the chat room");
        apply(new MessagePostedEvent(command.getParticipant(), roomId, command.getMessage()));
    }

    @EventSourcingHandler
    protected void on(final RoomCreatedEvent event)
    {
        this.roomId = event.getRoomId();
        this.participants = new HashSet<>();
    }

    @EventSourcingHandler
    protected void on(final ParticipantJoinedRoomEvent event)
    {
        this.participants.add(event.getParticipant());
    }

    @EventSourcingHandler
    protected void on(final ParticipantLeftRoomEvent event)
    {
        this.participants.remove(event.getParticipant());
    }
}
