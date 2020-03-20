package io.axoniq.labs.chat.restapi;

import java.util.UUID;
import java.util.concurrent.Future;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import io.axoniq.labs.chat.coreapi.CreateRoomCommand;
import io.axoniq.labs.chat.coreapi.JoinRoomCommand;
import io.axoniq.labs.chat.coreapi.LeaveRoomCommand;
import io.axoniq.labs.chat.coreapi.PostMessageCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommandController
{
    private final CommandGateway commandGateway;

    public CommandController(final CommandGateway commandGateway)
    {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/rooms")
    public Future<String> createChatRoom(@RequestBody @Valid final Room room)
    {
        Assert.notNull(room.getName(), "name is mandatory for a chatroom");
        final String roomId = room.getRoomId() == null ? UUID.randomUUID().toString() : room.getRoomId();
        return commandGateway.send(new CreateRoomCommand(roomId, room.getName()));
    }

    @PostMapping("/rooms/{roomId}/participants")
    public Future<Void> joinChatRoom(@PathVariable final String roomId, @RequestBody @Valid final Participant participant)
    {
        Assert.notNull(participant.getName(), "name is mandatory for a chatroom");
        return commandGateway.send(new JoinRoomCommand(participant.getName(), roomId));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public Future<Void> postMessage(@PathVariable final String roomId, @RequestBody @Valid final Message message)
    {
        Assert.notNull(message.getName(), "'name' missing - please provide a participant name");
        Assert.notNull(message.getMessage(), "'message' missing - please provide a message to post");
        return commandGateway.send(new PostMessageCommand(message.getName(), roomId, message.getMessage()));
    }

    @DeleteMapping("/rooms/{roomId}/participants")
    public Future<Void> leaveChatRoom(@PathVariable final String roomId, @RequestBody @Valid final Participant participant)
    {
        Assert.notNull(participant.getName(), "name is mandatory for a chatroom");
        return commandGateway.send(new LeaveRoomCommand(participant.getName(), roomId));
    }

    public static class Message
    {
        @NotEmpty
        private String name;

        @NotEmpty
        private String message;

        public String getName()
        {
            return name;
        }

        public void setName(final String name)
        {
            this.name = name;
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
