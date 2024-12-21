package org.ltm.meetingappv2serverjava.repository;

import org.ltm.meetingappv2serverjava.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);
}
