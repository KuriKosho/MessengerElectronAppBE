package org.ltm.meetingappv2serverjava.repository;

import org.ltm.meetingappv2serverjava.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends MongoRepository<Message, String> {

}
