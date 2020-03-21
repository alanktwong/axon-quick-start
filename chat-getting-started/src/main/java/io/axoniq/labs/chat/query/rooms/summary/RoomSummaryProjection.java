package io.axoniq.labs.chat.query.rooms.summary;

import java.util.List;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import io.axoniq.labs.chat.coreapi.RoomQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class RoomSummaryProjection
{
    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(final RoomSummaryRepository roomSummaryRepository)
    {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    // TODO: Create some event handlers that update this model when necessary
    @EventHandler
    public void on(final RoomCreatedEvent event)
    {
        checkNotNull(event.getRoomId());
        checkNotNull(event.getName());

        final var roomSummary = new RoomSummary(event.getRoomId(), event.getName());
        roomSummaryRepository.save(roomSummary);
    }

    // TODO: Create the query handler to read data from this model
    @QueryHandler
    public List<RoomSummary> handle(final AllRoomsQuery query)
    {
        checkNotNull(query);
        return roomSummaryRepository.findAllById(query.getRoomIds());
    }

    @QueryHandler
    public RoomSummary handle(final RoomQuery query)
    {
        checkNotNull(query);
        return roomSummaryRepository.findById(query.getRoomId()).orElse(null);
    }
}
