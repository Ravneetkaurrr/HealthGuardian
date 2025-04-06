package com.user.management.repository;

import com.user.management.model.EmailDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDetailsRepository extends MongoRepository<EmailDetails,String> {
    EmailDetails findByEmailType(String emailType);
}

