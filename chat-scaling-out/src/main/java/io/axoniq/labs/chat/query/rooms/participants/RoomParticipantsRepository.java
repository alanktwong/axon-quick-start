package io.axoniq.labs.chat.query.rooms.participants;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomParticipantsRepository extends JpaRepository<RoomParticipant, Long>
{
    List<RoomParticipant> findRoomParticipantsByRoomId(String roomId);

    void deleteByParticipantAndRoomId(String participant, String roomId);
}
