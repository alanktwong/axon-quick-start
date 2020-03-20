package io.axoniq.labs.chat.query.rooms.summary;

import java.util.List;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class RoomSummaryProjection
{
    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(final RoomSummaryRepository roomSummaryRepository)
    {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @QueryHandler
    public List<RoomSummary> on(final AllRoomsQuery query)
    {
        return roomSummaryRepository.findAll();
    }

    @EventHandler
    public void on(final RoomCreatedEvent event)
    {
        roomSummaryRepository.save(new RoomSummary(event.getRoomId(), event.getName()));
    }

    @EventHandler
    public void on(final ParticipantJoinedRoomEvent event)
    {
        roomSummaryRepository.getOne(event.getRoomId()).addParticipant();
    }

    @EventHandler
    public void on(final ParticipantLeftRoomEvent event)
    {
        roomSummaryRepository.getOne(event.getRoomId()).removeParticipant();
    }
}
