package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomParticipantsQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class RoomParticipantsProjection
{
    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(final RoomParticipantsRepository repository)
    {
        this.repository = repository;
    }

    // TODO: Create some event handlers that update this model when necessary
    @EventHandler
    public void on(final ParticipantJoinedRoomEvent event)
    {
        final var roomId = event.getRoomId();
        checkNotNull(roomId);
        final var participant = event.getParticipant();
        checkNotNull(participant);

        final var roomParticipant = new RoomParticipant(roomId, participant);
        repository.save(roomParticipant);
        // increment number of participants in the room?
    }

    @EventHandler
    public void on(final ParticipantLeftRoomEvent event)
    {
        final var roomId = event.getRoomId();
        checkNotNull(roomId);
        final var participant = event.getParticipant();
        checkNotNull(participant);
        repository.deleteByParticipantAndRoomId(participant, roomId);
        // decrement number of participants in the room?
    }

    // TODO: Create the query handler to read data from this model
    @QueryHandler
    public void handle(final RoomParticipantsQuery query)
    {
        final var roomId = query.getRoomId();
        checkNotNull(roomId);

        repository.findRoomParticipantsByRoomId(roomId);
    }
}
