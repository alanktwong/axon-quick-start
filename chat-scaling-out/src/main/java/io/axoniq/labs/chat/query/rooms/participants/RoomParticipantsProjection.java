package io.axoniq.labs.chat.query.rooms.participants;

import java.util.List;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomParticipantsQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class RoomParticipantsProjection
{
    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(final RoomParticipantsRepository repository)
    {
        this.repository = repository;
    }

    @QueryHandler
    public List<String> on(final RoomParticipantsQuery query)
    {
        return repository.findRoomParticipantsByRoomId(query.getRoomId())
            .stream()
            .map(RoomParticipant::getParticipant).sorted().collect(toList());
    }

    @EventHandler
    public void on(final ParticipantJoinedRoomEvent event)
    {
        repository.save(new RoomParticipant(event.getRoomId(), event.getParticipant()));
    }

    @EventHandler
    public void on(final ParticipantLeftRoomEvent event)
    {
        repository.deleteByParticipantAndRoomId(event.getParticipant(), event.getRoomId());
    }
}
