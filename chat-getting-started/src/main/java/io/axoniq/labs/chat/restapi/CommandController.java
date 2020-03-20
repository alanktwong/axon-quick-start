package io.axoniq.labs.chat.restapi;

import java.util.concurrent.Future;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommandController
{
    private final CommandGateway commandGateway;

    public CommandController(@SuppressWarnings("SpringJavaAutowiringInspection") final CommandGateway commandGateway)
    {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/rooms")
    public Future<String> createChatRoom(@RequestBody @Valid final Room room)
    {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/rooms/{roomId}/participants")
    public Future<Void> joinChatRoom(@PathVariable final String roomId, @RequestBody @Valid final Participant participant)
    {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/rooms/{roomId}/messages")
    public Future<Void> postMessage(@PathVariable final String roomId, @RequestBody @Valid final PostMessageRequest message)
    {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @DeleteMapping("/rooms/{roomId}/participants")
    public Future<Void> leaveChatRoom(@PathVariable final String roomId, @RequestBody @Valid final Participant participant)
    {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static class PostMessageRequest
    {
        @NotEmpty
        private String participant;

        @NotEmpty
        private String message;

        public String getParticipant()
        {
            return participant;
        }

        public void setParticipant(final String participant)
        {
            this.participant = participant;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(final String message)
        {
            this.message = message;
        }
    }

    public static class Participant
    {
        @NotEmpty
        private String name;

        public String getName()
        {
            return name;
        }

        public void setName(final String name)
        {
            this.name = name;
        }
    }

    public static class Room
    {
        private String roomId;

        @NotEmpty
        private String name;

        public String getRoomId()
        {
            return roomId;
        }

        public void setRoomId(final String roomId)
        {
            this.roomId = roomId;
        }

        public String getName()
        {
            return name;
        }

        public void setName(final String name)
        {
            this.name = name;
        }
    }
}
