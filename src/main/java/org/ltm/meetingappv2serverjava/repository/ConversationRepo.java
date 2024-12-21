package org.ltm.meetingappv2serverjava.repository;

import org.ltm.meetingappv2serverjava.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepo extends MongoRepository<Conversation, String> {
    @Query("{ $or: [ { 'user1' : ?0, 'user2' : ?1 }, { 'user1' : ?1, 'user2' : ?0 } ] }")
    Conversation findByUser1AndUser2(String user1, String user2);
    @Query("{ 'participants' : { $all : ?0 } }")
    Optional<Conversation> findByParticipantsContainingAll(List<String> participants);
    @Query("{ 'participants' : ?0 }")
    List<Conversation> findByParticipantsId(String userId);
    @Query("{ 'participants' : { $all : [ ?0, ?1 ] } }")
    Optional<Conversation> findByParticipants(String senderId, String receiverId);
}
