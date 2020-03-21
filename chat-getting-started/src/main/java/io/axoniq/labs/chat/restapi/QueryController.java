package io.axoniq.labs.chat.restapi;

import java.util.List;
import java.util.concurrent.Future;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.RoomMessagesQuery;
import io.axoniq.labs.chat.coreapi.RoomParticipantsQuery;
import io.axoniq.labs.chat.query.rooms.messages.ChatMessage;
import io.axoniq.labs.chat.query.rooms.summary.RoomSummary;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class QueryController
{
    private final QueryGateway gateway;

    public QueryController(final QueryGateway gateway)
    {
        this.gateway = gateway;
    }

    @GetMapping("rooms")
    public Future<List<RoomSummary>> listRooms()
    {
        // TODO: Send a query for this API call
        final var query = new AllRoomsQuery();
        return gateway.query(query, ResponseTypes.multipleInstancesOf(RoomSummary.class));
    }

    @GetMapping("/rooms/{roomId}/participants")
    public Future<List<String>> participantsInRoom(@PathVariable final String roomId)
    {
        // TODO: Send a query for this API call
        final var query = new RoomParticipantsQuery(roomId);
        return gateway.query(query, ResponseTypes.multipleInstancesOf(String.class));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Future<List<ChatMessage>> roomMessages(@PathVariable final String roomId)
    {
        // TODO: Send a query for this API call
        final var query = new RoomMessagesQuery(roomId);
        return gateway.query(query, ResponseTypes.multipleInstancesOf(ChatMessage.class));
    }

    @GetMapping(value = "/rooms/{roomId}/messages/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> subscribeRoomMessages(@PathVariable final String roomId)
    {
        // TODO: Send a subscription query for this API call
        final var query = new RoomMessagesQuery(roomId);
        final var subscriptionResult = gateway.subscriptionQuery(query, ResponseTypes.multipleInstancesOf(ChatMessage.class),
            ResponseTypes.instanceOf(ChatMessage.class));
        return subscriptionResult.updates();
    }
}
