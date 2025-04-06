package com.user.management.repository;

import com.user.management.model.User;
import com.user.management.request.UserRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByPhoneNo(String phoneNo);
}
